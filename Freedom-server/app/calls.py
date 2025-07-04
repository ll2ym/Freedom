from fastapi import APIRouter, WebSocket, WebSocketDisconnect, Depends, HTTPException, status
from typing import Dict
from services.session_manager import SessionManager
from utils.config import ICE_SERVERS
from utils.auth import verify_jwt_token

router = APIRouter()
session_manager = SessionManager()

@router.get("/ice-servers")
async def get_ice_servers():
    """
    Returns list of STUN/TURN servers for ICE negotiation.
    """
    return {"iceServers": ICE_SERVERS}


@router.websocket("/ws/signaling/{room_id}/{peer_id}")
async def signaling(
    websocket: WebSocket,
    room_id: str,
    peer_id: str,
    token_payload: dict = Depends(verify_jwt_token)
):
    """
    WebSocket signaling endpoint for WebRTC peer communication.
    - room_id: unique room to route messages within
    - peer_id: identifier of the current client
    - token_payload: JWT decoded token, used to authorize user
    """
    await session_manager.connect(room_id, peer_id, websocket)

    try:
        while True:
            data = await websocket.receive_json()

            # Optionally validate message structure
            if "type" not in data:
                await websocket.send_json({"error": "Missing message type"})
                continue

            # Forward signaling data to other peers in the same room
            await session_manager.broadcast(room_id, peer_id, data)

    except WebSocketDisconnect:
        await session_manager.disconnect(room_id, peer_id)

    except Exception as e:
        await websocket.send_json({"error": str(e)})
        await session_manager.disconnect(room_id, peer_id)
