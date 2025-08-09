from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, Field
from sqlalchemy.orm import Session
from passlib.context import CryptContext
from jose import jwt
from datetime import datetime, timedelta

from utils.db import get_db
from utils.config import JWT_SECRET, JWT_ALGORITHM, ACCESS_TOKEN_EXPIRE_MINUTES
from models.user import User
from services.invite import validate_invite
from utils.token import get_current_user

router = APIRouter()
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
ALGORITHM = "HS256"

# ==== Request/Response Models ====

class RegisterRequest(BaseModel):
    username: str = Field(..., min_length=3, max_length=20)
    password: str = Field(..., min_length=8, max_length=100)
    invite_code: str

class LoginRequest(BaseModel):
    username: str = Field(..., min_length=3, max_length=20)
    password: str = Field(..., min_length=8, max_length=100)

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"

class ProfileUpdateRequest(BaseModel):
    display_name: str = Field(..., max_length=50)
    status: str = Field(..., max_length=150)

class UserResponse(BaseModel):
    username: str
    display_name: str | None
    status: str | None

# ==== Helper Functions ====

def create_access_token(data: dict, expires_delta: timedelta = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, JWT_SECRET, algorithm=JWT_ALGORITHM)

def validate_password_complexity(password: str):
    if not any(char.isdigit() for char in password):
        raise HTTPException(status_code=400, detail="Password must contain at least one digit.")
    if not any(char.isalpha() for char in password):
        raise HTTPException(status_code=400, detail="Password must contain at least one letter.")
    return True

# ==== Routes ====

@router.post("/register", response_model=TokenResponse)
def register(req: RegisterRequest, db: Session = Depends(get_db)):
    if not validate_invite(req.invite_code):
        raise HTTPException(status_code=400, detail="Invalid invite code")

    validate_password_complexity(req.password)

    existing = db.query(User).filter_by(username=req.username).first()
    if existing:
        raise HTTPException(status_code=400, detail="Username already exists")

    user = User(
        username=req.username,
        password_hash=pwd_context.hash(req.password),
        registered_at=datetime.utcnow()
    )
    db.add(user)
    db.commit()
    db.refresh(user)

    token = create_access_token({"sub": user.username})
    return TokenResponse(access_token=token)

@router.post("/login", response_model=TokenResponse)
def login(req: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter_by(username=req.username).first()
    if not user or not pwd_context.verify(req.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")

    token = create_access_token({"sub": user.username})
    return TokenResponse(access_token=token)

@router.get("/me", response_model=UserResponse)
def get_me(current_user: User = Depends(get_current_user)):
    return current_user

@router.put("/me", response_model=UserResponse)
def update_me(req: ProfileUpdateRequest, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    current_user.display_name = req.display_name
    current_user.status = req.status
    db.commit()
    db.refresh(current_user)
    return current_user
