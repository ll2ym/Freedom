import os
import json
import warnings
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# === App Info ===
APP_NAME = os.getenv("APP_NAME", "FreedomServer")
APP_ENV = os.getenv("APP_ENV", "development")
APP_PORT = int(os.getenv("APP_PORT", 8000))

# === Database ===
if os.getenv("APP_ENV") == "test":
    DB_URL = "sqlite:///:memory:"
else:
    DB_URL = os.getenv("DB_URL", "sqlite:///./freedom.db")

# === JWT ===
JWT_SECRET = os.getenv("JWT_SECRET", "change_this_secret")
JWT_ALGORITHM = os.getenv("JWT_ALGORITHM", "HS256")
JWT_EXPIRE_MINUTES = int(os.getenv("JWT_EXPIRE_MINUTES", 1440))

# Security warning for default JWT secret in production
if APP_ENV == "production" and JWT_SECRET == "change_this_secret":
    warnings.warn("SECURITY WARNING: Default JWT_SECRET is being used in a production environment. Please set a strong, unique secret in your .env file.", UserWarning)

# === CORS ===
CORS_ALLOWED_ORIGINS_str = os.getenv("CORS_ALLOWED_ORIGINS", "")
CORS_ALLOWED_ORIGINS = [origin.strip() for origin in CORS_ALLOWED_ORIGINS_str.split(",") if origin.strip()]
if not CORS_ALLOWED_ORIGINS:
    CORS_ALLOWED_ORIGINS = ["*"] if APP_ENV == "development" else []


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
