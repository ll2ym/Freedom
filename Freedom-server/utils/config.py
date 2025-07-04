import os
import json
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# === App Info ===
APP_NAME = os.getenv("APP_NAME", "FreedomServer")
APP_ENV = os.getenv("APP_ENV", "development")
APP_PORT = int(os.getenv("APP_PORT", 8000))

# === Database ===
DB_URL = os.getenv("DB_URL", "sqlite:///./freedom.db")

# === JWT ===
JWT_SECRET = os.getenv("JWT_SECRET", "change_this_secret")
JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
JWT_EXPIRE_MINUTES = int(os.getenv("JWT_EXPIRE_MINUTES", 1440))

# === Encryption ===
# Optional: a base64-encoded 256-bit key for AES-GCM
ENCRYPTION_KEY = os.getenv("ENCRYPTION_KEY")

# === WebRTC ICE Servers ===
# Must be valid JSON string
ICE_SERVERS = json.loads(os.getenv(
    "ICE_SERVERS",
    '[{"urls": "stun:stun.l.google.com:19302"}]'
))

# === Rate Limiting ===
RATE_LIMIT_GLOBAL = os.getenv("RATE_LIMIT_GLOBAL", "100/minute")
RATE_LIMIT_LOGIN = os.getenv("RATE_LIMIT_LOGIN", "5/minute")
RATE_LIMIT_WS = os.getenv("RATE_LIMIT_WS", "50/minute")

# === Security Mode ===
INVITE_ONLY = os.getenv("INVITE_ONLY", "true").lower() == "true"
