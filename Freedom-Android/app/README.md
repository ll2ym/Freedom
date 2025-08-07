# Freedom Android App

**Freedom** is a private, secure, invite-only messaging application for Android 13+. It uses end-to-end encryption (based on Signal Protocol), supports IP-to-IP calls, and works without Google or Firebase services.

> 🦄 *“It is human right for secure communications!”*

---

## 🔐 Features

- End-to-end encrypted messaging
- Forward secrecy & screen security
- Secure local storage (SQLCipher + EncryptedSharedPreferences)
- Invite-only registration (username-based)
- IP-to-IP audio/video calling (WebRTC)
- Small private groups (max 40 members)
- Modern UI using Jetpack Compose
- No phone number required
- No Firebase, no Google analytics

---

## 🛠 Requirements

- Android Studio Giraffe or newer
- JDK 17+
- Android 13+ device or emulator
- Gradle 8.2+
- Git
- A running instance of the [Freedom Server](<path-to-server-readme>)

---

## 🚀 Getting Started

### 1. Set Up the Server

Before you can run the Android app, you need to have the `Freedom-server` running. Please follow the instructions in the [server's README file](../README.md) to set up and run the server.

### 2. Configure the Android App

Once the server is running, you need to configure the Android app to connect to it.

1.  Open the `Freedom-Android/app/src/main/java/com/freedom/utils/Constants.kt` file.
2.  Change the `BASE_URL` to the IP address of your server. If you are running the server on your local machine and using an Android emulator, the IP address should be `http://10.0.2.2:8000/`.
3.  Update the `CHAT_SOCKET_URL` and `CALL_SOCKET_URL` to use the same IP address.

### 3. Build and Run the App

You can build and run the app using Android Studio or from the command line.

**Android Studio:**

1.  Open the `Freedom-Android` project in Android Studio.
2.  Create a `local.properties` file in the root of the `Freedom-Android` directory and add the following line, replacing `<path-to-your-sdk>` with the actual path to your Android SDK:
    ```
    sdk.dir=<path-to-your-sdk>
    ```
3.  Sync the project with Gradle.
4.  Run the app on an emulator or a connected device.

**Command Line:**

1.  Create a `local.properties` file in the root of the `Freedom-Android` directory and add the following line, replacing `<path-to-your-sdk>` with the actual path to your Android SDK:
    ```
    sdk.dir=<path-to-your-sdk>
    ```
2.  Open a terminal in the `Freedom-Android` directory.
3.  Run the following command to build the app:
    ```bash
    ./gradlew assembleDebug
    ```
4.  The APK will be located in `Freedom-Android/app/build/outputs/apk/debug/`. You can install it on a device using `adb`.

---

## Dependencies

This project uses several open-source libraries, including:

-   [Jetpack Compose](https://developer.android.com/jetpack/compose) for the UI
-   [Hilt](https://dagger.dev/hilt/) for dependency injection
-   [Retrofit](https://square.github.io/retrofit/) and [Ktor](https://ktor.io/) for networking
-   [Room](https://developer.android.com/training/data-storage/room) and [SQLCipher](https://www.zetetic.net/sqlcipher/) for the local database
-   [Signal Protocol](https://signal.org/docs/) for end-to-end encryption
-   [WebRTC](https://webrtc.org/) for audio and video calls
