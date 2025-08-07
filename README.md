# Freedom - Secure Communication Platform

This repository contains the source code for the Freedom secure communication platform, which consists of two main components: an Android client and a FastAPI server.

## 🚀 Overview

-   **Freedom-Android**: A native Android application built with Kotlin and Jetpack Compose. It provides end-to-end encrypted messaging, group chats, and IP-to-IP calling.
-   **Freedom-server**: A Python backend server built with FastAPI. It handles user authentication, WebSocket connections for real-time messaging, and WebRTC signaling for calls.

## 📂 Project Structure

-   `Freedom-Android/`: Contains the source code for the Android mobile application.
-   `Freedom-server/`: Contains the source code for the Python FastAPI backend server.

## 🛠️ Getting Started

For detailed instructions on how to set up, configure, and run each component, please refer to the `README.md` file within each directory:

-   **[Freedom Server Instructions](./Freedom-server/README.md)**
-   **[Freedom Android App Instructions](./Freedom-Android/app/README.md)**

### Quick Start: Server (Production)

To get the server up and running quickly in a production-like environment using Docker, follow these steps:

1.  Navigate to the `Freedom-server` directory.
2.  Copy the `.env.example` file to `.env` and configure the variables.
3.  Run `docker-compose -f docker-compose.prod.yml up --build -d`.

### Quick Start: Android

To build the Android app, you will need the Android SDK.

1.  Navigate to the `Freedom-Android` directory.
2.  Create a `local.properties` file with the path to your Android SDK (e.g., `sdk.dir=/path/to/your/sdk`).
3.  Run `./gradlew assembleDebug`.

## 📜 Development Notes

-   This is **NOT** a Node.js project. Do not use `npm` commands.
-   Use Gradle for all Android development tasks.
-   Use `pip` and `docker-compose` for Python server development.