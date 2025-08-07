from fastapi import FastAPI
from middleware.cors_headers import add_cors_and_security_headers
from utils.rate_limit import setup_rate_limit
from utils.config import APP_NAME
from app import auth, chat, groups, calls

def create_app() -> FastAPI:
    app = FastAPI(
        title=APP_NAME,
        description="Freedom - Secure Messaging Server",
        version="1.0.0"
    )

    # Apply security headers and CORS
    add_cors_and_security_headers(app)

    # Rate limiting middleware
    setup_rate_limit(app)

    # Include API routers
    app.include_router(auth.router, prefix="/auth", tags=["Auth"])
    app.include_router(chat.chat_router, prefix="/chat", tags=["Chat"])
    app.include_router(groups.router, prefix="/groups", tags=["Groups"])
    app.include_router(calls.router, prefix="/calls", tags=["Calls / Signaling"])

    return app
