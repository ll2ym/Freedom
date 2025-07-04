# Freedom - Secure Communication Platform

This project consists of two main components:

## Freedom-Android
Android application built with Kotlin and Jetpack Compose.

### Building the Android App
```bash
cd Freedom-Android
./gradlew build
```

### Running Tests
```bash
cd Freedom-Android
./gradlew test
```

## Freedom-Server
Python FastAPI backend server.

### Running the Server (Development)
```bash
cd Freedom-server
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### Running with Docker
```bash
cd Freedom-server
docker build -t freedom-server .
docker run -p 8000:8000 freedom-server
```

### Running with Docker Compose
```bash
cd Freedom-server
docker-compose up --build
```

## Project Structure
- `Freedom-Android/`: Android mobile application
- `Freedom-server/`: Python FastAPI backend server

## Development Notes
- This is NOT a Node.js project - do not use npm commands
- Use Gradle for Android development
- Use pip/Docker for Python server development