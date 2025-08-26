import os
import json
import hashlib
from datetime import datetime, timedelta, timezone

INVITE_STORE_PATH = "services/invite_codes.json"

# === Create & Load Invite Store ===

def _load_invite_store() -> dict:
    if not os.path.exists(INVITE_STORE_PATH):
        return {}
    with open(INVITE_STORE_PATH, "r") as f:
        return json.load(f)

def _save_invite_store(data: dict):
    with open(INVITE_STORE_PATH, "w") as f:
        json.dump(data, f, indent=2)

# === Generate Invite Code ===

def generate_invite_code(creator: str, valid_days: int = 30) -> str:
    """Create a new invite code with expiration and store it"""
    store = _load_invite_store()
    raw_code = f"{creator}-{datetime.now(timezone.utc).isoformat()}-{os.urandom(4).hex()}"
    code = hashlib.sha256(raw_code.encode()).hexdigest()[:12]

    store[code] = {
        "creator": creator,
        "created_at": datetime.now(timezone.utc).isoformat(),
        "expires_at": (datetime.now(timezone.utc) + timedelta(days=valid_days)).isoformat(),
        "used": False
    }
    _save_invite_store(store)
    return code

# === Validate Invite Code ===

def validate_invite(code: str) -> bool:
    store = _load_invite_store()
    invite = store.get(code)
    if not invite:
        return False
    if invite["used"]:
        return False
    if datetime.now(timezone.utc) > datetime.fromisoformat(invite["expires_at"]):
        return False
    return True

# === Mark Invite Code as Used ===

def mark_invite_used(code: str):
    store = _load_invite_store()
    if code in store:
        store[code]["used"] = True
        _save_invite_store(store)


# === Update auth.py to use mark_invite_used ===

# In app/auth.py, within the register function, after db.refresh(user):
# from services.invite import validate_invite, mark_invite_used
# ...
#    db.refresh(user)
#    mark_invite_used(req.invite_code) # Add this line
#    token = create_access_token({"sub": user.username})
# ...


