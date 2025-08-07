import json
import pytest
from services import invite

@pytest.fixture
def authenticated_tokens(client):
    # Register and login two users
    invite_code = invite.generate_invite_code("test-admin")
    client.post("/auth/register", json={"username": "alice", "password": "password", "invite_code": invite_code})
    client.post("/auth/register", json={"username": "bob", "password": "password", "invite_code": invite_code})
    alice_token = client.post("/auth/login", json={"username": "alice", "password": "password"}).json()["access_token"]
    bob_token = client.post("/auth/login", json={"username": "bob", "password": "password"}).json()["access_token"]
    return {"alice": alice_token, "bob": bob_token}

@pytest.fixture
def websocket_clients(client, authenticated_tokens):
    alice_token = authenticated_tokens["alice"]
    bob_token = authenticated_tokens["bob"]
    with client.websocket_connect(f"/chat/ws?token={alice_token}") as alice_ws, \
         client.websocket_connect(f"/chat/ws?token={bob_token}") as bob_ws:
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

def test_send_to_offline_user(client, authenticated_tokens):
    alice_token = authenticated_tokens["alice"]
    with client.websocket_connect(f"/chat/ws?token={alice_token}") as alice_ws:
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
