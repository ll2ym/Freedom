from typing import Dict
from fastapi import WebSocket


class SessionManager:
    def __init__(self):
        # Format: room_id -> {peer_id -> WebSocket}
        self.active_rooms: Dict[str, Dict[str, WebSocket]] = {}

    async def connect(self, room_id: str, peer_id: str, websocket: WebSocket):
        """
        Accepts WebSocket connection and registers the peer in a room.
        """
        await websocket.accept()
        if room_id not in self.active_rooms:
            self.active_rooms[room_id] = {}
        self.active_rooms[room_id][peer_id] = websocket
        print(f"Peer '{peer_id}' joined room '{room_id}'")

    async def disconnect(self, room_id: str, peer_id: str):
        """
        Removes the peer from the room and deletes the room if empty.
        """
        if room_id in self.active_rooms:
            if peer_id in self.active_rooms[room_id]:
                del self.active_rooms[room_id][peer_id]
                print(f"Peer '{peer_id}' left room '{room_id}'")
            if not self.active_rooms[room_id]:
                del self.active_rooms[room_id]
                print(f"Room '{room_id}' deleted (empty)")

    async def broadcast(self, room_id: str, sender_id: str, data: dict):
        """
        Forwards a message to all other peers in the same room.
        """
        if room_id not in self.active_rooms:
            return

        for peer_id, ws in self.active_rooms[room_id].items():
            if peer_id != sender_id:
                try:
                    await ws.send_json({
                        "from": sender_id,
                        "data": data
                    })
                except Exception as e:
                    print(f"Error sending to {peer_id} in {room_id}: {e}")
