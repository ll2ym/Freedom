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
    invite_code_1 = invite.generate_invite_code("test-admin", valid_days=1)
    client.post("/auth/register", json={
        "username": "testuser1",
        "password": "securePass123",
        "invite_code": invite_code_1
    })

    # Use a new, valid invite code for the second attempt
    invite_code_2 = invite.generate_invite_code("test-admin", valid_days=1)
    response = client.post("/auth/register", json={
        "username": "testuser1",
        "password": "anotherPassword",
        "invite_code": invite_code_2
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

def test_get_me_success(client):
    # Register and login to get a token
    invite_code = invite.generate_invite_code("test-admin", valid_days=1)
    username = "me_user"
    password = "securePassword"
    client.post("/auth/register", json={
        "username": username,
        "password": password,
        "invite_code": invite_code
    })
    login_res = client.post("/auth/login", json={"username": username, "password": password})
    token = login_res.json()["access_token"]

    # Call /me endpoint
    headers = {"Authorization": f"Bearer {token}"}
    response = client.get("/auth/me", headers=headers)

    assert response.status_code == 200
    assert response.json()["username"] == username

def test_login_user_not_found(client):
    response = client.post("/auth/login", json={
        "username": "nonexistent",
        "password": "anyPass"
    })
    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid credentials"
