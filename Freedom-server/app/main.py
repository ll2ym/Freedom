from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from middleware.cors_headers import add_cors_and_security_headers
from utils.rate_limit import setup_rate_limit
from utils.config import APP_NAME, APP_ENV, APP_PORT
from utils.db import create_tables
from app import auth, chat, groups, calls

# Create database tables
create_tables()

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
app.include_router(chat.router, prefix="/chat", tags=["Chat"])
app.include_router(groups.router, prefix="/groups", tags=["Groups"])
app.include_router(calls.router, prefix="/calls", tags=["Calls / Signaling"])

# Health check route
@app.get("/")
async def root():
    return {
        "status": "ok", 
        "app": APP_NAME,
        "env": APP_ENV,
        "message": "Freedom Server is running"
    }

@app.get("/health")
async def health_check():
    return {"status": "healthy", "timestamp": "2024-01-01T00:00:00Z"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=APP_PORT)