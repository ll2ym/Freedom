# 🦄 Freedom – Secure Messaging Server

**Freedom** is a highly secure, invite-only, end-to-end encrypted messaging server inspired by Signal.
This backend is designed for small groups (30–40 users), with support for encrypted group chats, IP-to-IP calling, and no dependency on Google or Apple services.

> 🛡️ *"It is human right for secure communications!"*

---

## 📦 Features

- ✅ **End-to-end encrypted** messages (client-side)
- ✅ **Username-based** registration (no phone number or email)
- ✅ **Invite-only** system for account creation
- ✅ **Group chat support**
- ✅ **IP-to-IP audio/video calling (via WebRTC signaling)**
- ✅ **No Firebase / Google / Apple push**
- ✅ **Modern FastAPI backend**
- ✅ **Dockerized deployment** for both development and production

---

## 🚀 Getting Started

### Development Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd Freedom-server
    ```

2.  **Create a virtual environment and install dependencies:**
    ```bash
    python -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt
    pip install -r requirements.dev.txt
    ```

3.  **Run the development server:**
    The development server uses a local SQLite database by default.
    ```bash
    uvicorn app.main:app --reload
    ```
    The server will be available at `http://127.0.0.1:8000`.

### Production Deployment with Docker

This is the recommended way to run the server in production. It uses Docker and Docker Compose to set up the FastAPI server and a PostgreSQL database.

1.  **Create a `.env` file:**
    Copy the `.env.example` file to `.env` and fill in the required values.
    ```bash
    cp .env.example .env
    ```
    Make sure to set a strong `POSTGRES_PASSWORD` and `JWT_SECRET`. For enhanced security, it is also recommended to create a dedicated database user with the minimum necessary permissions, rather than using a superuser.

2.  **Build and run the containers:**
    ```bash
    docker-compose -f docker-compose.prod.yml up --build -d
    ```
    The server will be available at `http://localhost:8000`.

---

## ⚙️ Configuration

The server is configured using environment variables. See the `.env.example` file for a complete list of available options.

-   `DATABASE_URL`: The connection string for the database. For production, this should point to a PostgreSQL database.
-   `JWT_SECRET`: A secret key for signing JWTs. This should be a long, random string.
-   `INVITE_ONLY`: Set to `true` to require an invite code for registration.

---

## API Documentation

The server provides automatic API documentation using Swagger UI and ReDoc. When the server is running, you can access them at:

-   **Swagger UI:** `http://127.0.0.1:8000/docs`
-   **ReDoc:** `http://127.0.0.1:8000/redoc`

### Key API Changes

-   The endpoint to get the current user's information has been moved from `/users/me` to `/auth/me`.
-   Messaging is now handled exclusively through WebSockets at the `/chat/ws` endpoint. The old REST-based messaging endpoints have been removed.

---

## 🗂️ Project Structure
freedom-server/
├── app/
│ ├── main.py # FastAPI app entry point
│ ├── auth.py # Registration/login endpoints
│ ├── chat.py # WebSocket chat
│ ├── groups.py # Group management
│ └── calls.py # WebRTC signaling
├── models/
│ ├── user.py # User model
│ ├── group.py # Group and members
│ └── message.py # Message model
├── services/
│ ├── encryption.py # AES/GCM crypto utils
│ ├── session_manager.py # Track live user sessions
│ └── invite.py # Invite code system
├── utils/
│ ├── config.py # Settings loader
│ ├── db.py # DB session setup
│ └── jwt.py # JWT generation/validation
├── tests/ # Pytest unit tests
├── requirements.txt
├── requirements.dev.txt
├── Dockerfile
├── Dockerfile.prod
├── docker-compose.yml
├── docker-compose.prod.yml
├── .env.example
└── README.md
