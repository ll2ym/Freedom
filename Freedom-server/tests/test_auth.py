import pytest
from services import invite

def test_register_success(client):
    invite_code = invite.generate_invite_code("test-admin", valid_days=1)
    response = client.post("/auth/register", json={
        "username": "testuser1",
        "password": "securePass123",
        "invite_code": invite_code
    })
    assert response.status_code == 200
    assert "access_token" in response.json()

def test_register_duplicate_username(client):
    invite_code = invite.generate_invite_code("test-admin", valid_days=1)
    client.post("/auth/register", json={
        "username": "testuser1",
        "password": "securePass123",
        "invite_code": invite_code
    })
    response = client.post("/auth/register", json={
        "username": "testuser1",
        "password": "anotherPassword",
        "invite_code": invite_code
    })
    assert response.status_code == 400
    assert response.json()["detail"] == "Username already exists"

def test_register_invalid_invite(client):
    response = client.post("/auth/register", json={
        "username": "testuser2",
        "password": "anotherPass",
        "invite_code": "invalidcode123"
    })
    assert response.status_code == 400
    assert response.json()["detail"] == "Invalid invite code"

def test_login_success(client):
    invite_code = invite.generate_invite_code("test-admin", valid_days=1)
    client.post("/auth/register", json={
        "username": "testuser1",
        "password": "securePass123",
        "invite_code": invite_code
    })
    response = client.post("/auth/login", json={
        "username": "testuser1",
        "password": "securePass123"
    })
    assert response.status_code == 200
    assert "access_token" in response.json()

def test_login_wrong_password(client):
    invite_code = invite.generate_invite_code("test-admin", valid_days=1)
    client.post("/auth/register", json={
        "username": "testuser1",
        "password": "securePass123",
        "invite_code": invite_code
    })
    response = client.post("/auth/login", json={
        "username": "testuser1",
        "password": "wrongPass"
    })
    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid credentials"

def test_login_user_not_found(client):
    response = client.post("/auth/login", json={
        "username": "nonexistent",
        "password": "anyPass"
    })
    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid credentials"
