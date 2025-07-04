from fastapi import FastAPI, Request, HTTPException
from slowapi import Limiter
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded

# Create a rate limiter instance based on client IP address
limiter = Limiter(key_func=get_remote_address)

# Function to initialize rate limiting on the app
def setup_rate_limit(app: FastAPI):
    app.state.limiter = limiter

    @app.exception_handler(RateLimitExceeded)
    async def rate_limit_exceeded_handler(request: Request, exc: RateLimitExceeded):
        raise HTTPException(
            status_code=429,
            detail="Too many requests. Please slow down."
        )
