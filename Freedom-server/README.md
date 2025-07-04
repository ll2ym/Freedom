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
- ✅ **Dockerized deployment**

---

## 🗂️ Project Structure
freedom-server/
├── app/
│ ├── main.py # FastAPI app entry point
│ ├── auth.py # Registration/login endpoints
│ ├── chat.py # WebSocket chat
│ ├── groups.py # Group management
│ ├── calls.py # WebRTC signaling
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
├── Dockerfile
├── docker-compose.yml
├── .env
└── README.md

