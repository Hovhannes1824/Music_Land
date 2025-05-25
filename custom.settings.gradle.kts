// This file is used to override Android Studio's internal AGP version
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.2.2"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("com.google.gms.google-services") version "4.4.1"
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useVersion("8.2.2")
            }
        }
    }
}

// Ensure this file is included by adding the following to your settings.gradle.kts:
// apply(from = "custom.settings.gradle.kts") 