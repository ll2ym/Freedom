plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.freedom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.freedom"
        minSdk = 33 // Android 13
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Required for WebRTC or native libs
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }

        // Enable Jetpack Compose
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.3"
        }

        packaging {
            resources {
                excludes += setOf(
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt"
                )
            }
        }
    }

    signingConfigs {
        create("release") {
            // Define in local.properties or use secure signing later
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core + Compose
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // WebRTC
    implementation("org.webrtc:google-webrtc:1.0.32006")

    // Signal Protocol (libsignal)
    implementation("org.whispersystems:signal-protocol-java:2.8.1")

    // Room + SQLCipher for encrypted DB
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    kapt("androidx.room:room-compiler:2.6.1")

    // Secure Preferences (EncryptedSharedPreferences)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Networking (optional for signaling, auth)
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-websockets:2.3.4")

    // Serialization (optional)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
}

