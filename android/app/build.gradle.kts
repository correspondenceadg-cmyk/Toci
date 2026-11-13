plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Signing

val tociDebug by signingConfigs.creating {
    storeFile = file("toci-debug.keystore")
    storePassword = "android"
    keyAlias = "androiddebugkey"
    keyPassword = "android"
}

android {
    namespace = "ws.tilda.sentryprotocol.toci"
    compileSdk = 35

    defaultConfig {
        applicationId = "ws.tilda.sentryprotocol.toci"
        minSdk = 30
        targetSdk = 35
        versionCode = (project.findProperty("toci.versionCode") as String?)?.toInt() ?: 1
        versionName = (project.findProperty("toci.versionName") as String?) ?: "0.0.0-snapshot"
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64") }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = tociDebug
        }
        debug {
            signingConfig = tociDebug
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
