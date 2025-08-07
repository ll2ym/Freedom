from app.core.app import create_app
from utils.db import create_tables
from utils.config import APP_ENV, APP_PORT
import uvicorn

app = create_app()

@app.on_event("startup")
def startup_event():
    if APP_ENV != "test":
        create_tables()

# Health check route
@app.get("/")
async def root():
    return {
        "status": "ok", 
        "app": "FreedomServer",
        "env": APP_ENV,
        "message": "Freedom Server is running"
    }

@app.get("/health")
async def health_check():
    return {"status": "healthy", "timestamp": "2024-01-01T00:00:00Z"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=APP_PORT)
