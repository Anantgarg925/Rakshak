plugins {
    alias(libs.plugins.android.application)
    // The Google Services plugin is applied here without "apply false"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.rakshak"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rakshak"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        mlModelBinding = true
    }
}

dependencies {

    // Core Android & Material
    implementation(libs.appcompat)
    implementation(libs.material) // Kept one Material dependency
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    implementation(libs.gson)

    // Firebase - BoM should be first
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    // REMOVED: implementation(libs.firebase.crashlytics.buildtools) // CRITICAL ERROR

    // Google Identity Services (Recommended)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation (libs.googleid)


    // Google Play Services
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth.v2140)

    implementation("com.google.android.gms:play-services-maps:18.2.0")


    // Networking
    implementation(libs.okhttp)
    implementation(libs.json)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.material.v1120)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)

    // Retrofit for networking
    implementation(libs.retrofit)
// Gson converter to handle JSON data
    implementation(libs.retrofit2.converter.gson)

    // For WebSockets
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.tensorflow:tensorflow-lite:2.9.0")
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.9.0")
}