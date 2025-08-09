import logging
from fastapi import APIRouter, WebSocket, WebSocketDisconnect, Depends, HTTPException
from sqlalchemy.orm import Session
from app.core.connections import ConnectionManager
from utils.db import get_db
from models.user import User
from utils.token import get_current_user_from_token

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

chat_router = APIRouter()
manager = ConnectionManager()

@chat_router.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket, token: str = None, db: Session = Depends(get_db)):
    if not token:
        logger.warning("WebSocket connection attempt without token.")
        await websocket.close(code=1008)
        return

    try:
        user = get_current_user_from_token(token, db)
    except HTTPException:
        logger.warning(f"WebSocket connection attempt with invalid token: {token}")
        await websocket.close(code=1008)
        return

    await manager.connect(websocket, user.username)
    logger.info(f"User '{user.username}' connected to chat WebSocket.")
    try:
        while True:
            data = await websocket.receive_json()
            if len(str(data)) > 4096: # Limit message size
                logger.warning(f"User '{user.username}' sent oversized message.")
                await websocket.send_json({"type": "error", "message": "Message size exceeds limit."})
                continue

            to = data.get("to")
            if to:
                message = {"from": user.username, "to": to, "type": "text", "payload": data.get("payload")}
                if not await manager.send_personal_message(message, to):
                    await websocket.send_json({"type": "error", "message": f"User '{to}' not online."})
            else:
                # Disabling broadcast for security reasons unless explicitly designed for a feature.
                # await manager.broadcast(data)
                logger.warning(f"User '{user.username}' attempted to broadcast a message.")
                await websocket.send_json({"type": "error", "message": "Broadcast not supported."})

    except WebSocketDisconnect:
        logger.info(f"User '{user.username}' disconnected from chat WebSocket.")
        manager.disconnect(user.username)
    except Exception as e:
        logger.error(f"An unexpected error occurred with user '{user.username}': {e}", exc_info=True)
        manager.disconnect(user.username)
