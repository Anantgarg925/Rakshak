// File: ./build.gradle.kts (the one in the root folder)

plugins {
    alias(libs.plugins.android.application) apply false
    // Add this line to declare the plugin for your whole project
    alias(libs.plugins.google.gms.google.services) apply false
}