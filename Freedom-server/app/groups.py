from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from typing import List
from sqlalchemy.orm import Session
from datetime import datetime, timezone

from utils.db import get_db
from models.group import Group, GroupMember
from models.user import User
from utils.jwt import get_current_user

router = APIRouter()

# === Request models ===

class CreateGroupRequest(BaseModel):
    name: str
    members: List[str]  # List of usernames to add

class AddMemberRequest(BaseModel):
    group_id: int
    username: str

# === Routes ===

@router.post("/create")
def create_group(req: CreateGroupRequest, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    existing = db.query(Group).filter_by(name=req.name).first()
    if existing:
        raise HTTPException(status_code=400, detail="Group name already exists")

    group = Group(
        name=req.name,
        owner_id=current_user.id,
        created_at=datetime.now(timezone.utc)
    )
    db.add(group)
    db.commit()
    db.refresh(group)

    # Add owner + members
    all_members = set(req.members + [current_user.username])
    for username in all_members:
        user = db.query(User).filter_by(username=username).first()
        if user:
            member = GroupMember(group_id=group.id, user_id=user.id)
            db.add(member)

    db.commit()
    return {"group_id": group.id, "name": group.name}

@router.post("/add-member")
def add_member(req: AddMemberRequest, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    group = db.query(Group).filter_by(id=req.group_id).first()
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")

    if group.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Only group owner can add members")

    user = db.query(User).filter_by(username=req.username).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    existing = db.query(GroupMember).filter_by(group_id=group.id, user_id=user.id).first()
    if existing:
        raise HTTPException(status_code=400, detail="User already in group")

    db.add(GroupMember(group_id=group.id, user_id=user.id))
    db.commit()
    return {"status": "user added"}

@router.get("/{group_id}/members", response_model=List[str])
def get_members(group_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    members = (
        db.query(User.username)
        .join(GroupMember, User.id == GroupMember.user_id)
        .filter(GroupMember.group_id == group_id)
        .all()
    )
    return [m[0] for m in members]
