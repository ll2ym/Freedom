from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from middleware.cors_headers import add_cors_and_security_headers
from utils.rate_limit import setup_rate_limit
from utils.config import APP_NAME, APP_ENV, APP_PORT
from app import auth, chat, groups, calls

app = FastAPI(title=APP_NAME)

# Apply security headers and CORS
add_cors_and_security_headers(app)

# Rate limiting middleware
setup_rate_limit(app)

# Include API routers
app.include_router(auth.router, prefix="/auth", tags=["Auth"])
app.include_router(chat.router, prefix="/chat", tags=["Chat"])
app.include_router(groups.router, prefix="/groups", tags=["Groups"])
app.include_router(calls.router, prefix="/calls", tags=["Calls / Signaling"])

# Health check route (optional)
@app.get("/")
async def root():
    return {"status": "ok", "env": APP_ENV}
