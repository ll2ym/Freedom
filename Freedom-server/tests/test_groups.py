import pytest
from fastapi.testclient import TestClient
from app.main import app
from services import invite

client = TestClient(app)

@pytest.fixture(scope="module")
def setup_users():
    code1 = invite.generate_invite_code("admin")
    code2 = invite.generate_invite_code("admin")

    r1 = client.post("/auth/register", json={
        "username": "alice",
        "password": "alicepass",
        "invite_code": code1
    })
    r2 = client.post("/auth/register", json={
        "username": "bob",
        "password": "bobpass",
        "invite_code": code2
    })

    token1 = r1.json()["access_token"]
    token2 = r2.json()["access_token"]

    return {
        "alice_token": token1,
        "bob_token": token2
    }

@pytest.fixture
def auth_headers(setup_users):
    return {
        "alice": {"Authorization": f"Bearer {setup_users['alice_token']}"},
        "bob": {"Authorization": f"Bearer {setup_users['bob_token']}"}
    }

def test_create_group(auth_headers):
    response = client.post("/groups/create", json={
        "name": "devs",
        "members": ["bob"]
    }, headers=auth_headers["alice"])
    assert response.status_code == 200
    assert "group_id" in response.json()

def test_add_member_success(auth_headers):
    # First, create a new group
    create = client.post("/groups/create", json={
        "name": "testers",
        "members": []
    }, headers=auth_headers["alice"])
    group_id = create.json()["group_id"]

    # Then add Bob
    add = client.post("/groups/add-member", json={
        "group_id": group_id,
        "username": "bob"
    }, headers=auth_headers["alice"])

    assert add.status_code == 200
    assert add.json()["status"] == "user added"

def test_add_member_by_non_owner(auth_headers):
    # Create group as Alice
    create = client.post("/groups/create", json={
        "name": "qa-team",
        "members": []
    }, headers=auth_headers["alice"])
    group_id = create.json()["group_id"]

    # Try to add as Bob
    add = client.post("/groups/add-member", json={
        "group_id": group_id,
        "username": "alice"
    }, headers=auth_headers["bob"])

    assert add.status_code == 403
    assert add.json()["detail"] == "Only group owner can add members"

def test_get_members(auth_headers):
    # Create group
    create = client.post("/groups/create", json={
        "name": "readers",
        "members": ["bob"]
    }, headers=auth_headers["alice"])
    group_id = create.json()["group_id"]

    # Get members
    response = client.get(f"/groups/{group_id}/members", headers=auth_headers["alice"])

    assert response.status_code == 200
    assert "alice" in response.json()
    assert "bob" in response.json()
