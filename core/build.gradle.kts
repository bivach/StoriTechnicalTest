plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt.plugin)
    kotlin("kapt")
}

android {
    namespace = "com.storitechnicaltest.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlinx.coroutines)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)

    // CameraX dependencies
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx) {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }
    implementation(libs.kotlinx.coroutines.play.services)

    testImplementation(libs.junit)
    testImplementation ("org.mockito:mockito-core:3.+")
}