from fastapi import APIRouter, WebSocket, WebSocketDisconnect, Depends
from sqlalchemy.orm import Session
from app.core.connections import ConnectionManager
from utils.db import get_db
from models.user import User
from utils.token import get_current_user_from_token

chat_router = APIRouter()
manager = ConnectionManager()

@chat_router.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket, token: str = None, db: Session = Depends(get_db)):
    if not token:
        await websocket.close(code=1008)
        return

    user = get_current_user_from_token(token, db)
    if not user:
        await websocket.close(code=1008)
        return

    await manager.connect(websocket, user.username)
    try:
        while True:
            data = await websocket.receive_json()
            to = data.get("to")
            if to:
                message = {"from": user.username, "to": to, "type": "text", "payload": data.get("payload")}
                if not await manager.send_personal_message(message, to):
                    await websocket.send_json({"type": "error", "message": f"User {to} not online"})
            else:
                await manager.broadcast(data)
    except WebSocketDisconnect:
        manager.disconnect(user.username)
    except Exception as e:
        print(e)
        manager.disconnect(user.username)
