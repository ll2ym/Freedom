import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.core.app import create_app
from utils.db import Base, get_db
import os
import json

SQLALCHEMY_DATABASE_URL = "sqlite:///./test.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

@pytest.fixture(scope="function")
def db():
    if not os.path.exists("services"):
        os.makedirs("services")
    with open("services/invite_codes.json", "w") as f:
        json.dump({}, f)
    Base.metadata.create_all(bind=engine)
    db = TestingSessionLocal()
    try:
        yield db
    finally:
        db.close()
        Base.metadata.drop_all(bind=engine)
        os.remove("services/invite_codes.json")
        os.rmdir("services")

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
