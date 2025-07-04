import json
import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

@pytest.fixture
def websocket_clients():
    with client.websocket_connect("/ws/alice") as alice_ws, \
         client.websocket_connect("/ws/bob") as bob_ws:
        yield alice_ws, bob_ws

def test_websocket_message_exchange(websocket_clients):
    alice_ws, bob_ws = websocket_clients

    # Alice sends message to Bob
    message_payload = {
        "to": "bob",
        "type": "text",
        "payload": {
            "message": "Hello Bob!",
            "timestamp": 1710000000000
        }
    }

    alice_ws.send_json(message_payload)

    # Bob receives it
    received = bob_ws.receive_json()

    assert received["from"] == "alice"
    assert received["type"] == "text"
    assert received["payload"]["message"] == "Hello Bob!"

def test_send_to_offline_user():
    with client.websocket_connect("/ws/alice") as alice_ws:
        message_payload = {
            "to": "charlie",
            "type": "text",
            "payload": {
                "message": "Hey Charlie!",
                "timestamp": 1710000000001
            }
        }
        alice_ws.send_json(message_payload)
        response = alice_ws.receive_json()

        assert response["type"] == "error"
        assert "not online" in response["message"]
