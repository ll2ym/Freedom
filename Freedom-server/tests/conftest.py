import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.core.app import create_app
from utils.db import Base, get_db
import os
import json
import shutil

SQLALCHEMY_DATABASE_URL = "sqlite:///./test.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

@pytest.fixture(scope="function")
def db(tmp_path, monkeypatch):
    # Create a temporary file for invite codes
    invite_file = tmp_path / "invite_codes.json"
    invite_file.write_text("{}")

    # Monkeypatch the hardcoded path in the invite service
    monkeypatch.setattr("services.invite.INVITE_STORE_PATH", str(invite_file))

    Base.metadata.create_all(bind=engine)
    db_session = TestingSessionLocal()
    try:
        yield db_session
    finally:
        db_session.close()
        Base.metadata.drop_all(bind=engine)

def override_get_db():
    db = TestingSessionLocal()
    try:
        yield db
    finally:
        db.close()

@pytest.fixture(scope="function")
def app(db):
    app = create_app()
    app.dependency_overrides[get_db] = lambda: db
    return app

@pytest.fixture(scope="function")
def client(app):
    return TestClient(app)
