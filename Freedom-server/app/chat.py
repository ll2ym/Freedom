from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from typing import Dict, List
import json

router = APIRouter()

# Store connected users and their WebSocket sessions
active_connections: Dict[str, WebSocket] = {}

@router.websocket("/ws/{username}")
async def chat_socket(websocket: WebSocket, username: str):
    await websocket.accept()
    active_connections[username] = websocket
    print(f"[CHAT] {username} connected.")

    try:
        while True:
            data = await websocket.receive_text()
            message = json.loads(data)

            to_user = message.get("to")
            msg_type = message.get("type")  # e.g., "text", "group", "delivery-receipt"
            payload = message.get("payload")

            if msg_type == "text" and to_user:
                # Forward message to recipient
                if to_user in active_connections:
                    await active_connections[to_user].send_json({
                        "from": username,
                        "type": "text",
                        "payload": payload
                    })
                else:
                    await websocket.send_json({
                        "type": "error",
                        "message": f"User {to_user} is not online"
                    })

            elif msg_type == "group":
                # Handle group message routing (optional: include DB lookup)
                recipients: List[str] = payload.get("recipients", [])
                for member in recipients:
                    if member in active_connections and member != username:
                        await active_connections[member].send_json({
                            "from": username,
                            "type": "group",
                            "group_id": payload.get("group_id"),
                            "payload": payload.get("message")
                        })

            else:
                await websocket.send_json({
                    "type": "error",
                    "message": "Invalid message type"
                })

    except WebSocketDisconnect:
        print(f"[CHAT] {username} disconnected.")
        active_connections.pop(username, None)
