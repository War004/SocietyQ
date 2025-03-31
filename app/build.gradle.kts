plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.CodeShark.SocietyQ"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.CodeShark.SocietyQ"
        minSdk = 31
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core & UI
    implementation(libs.coil.compose.v250)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material) // From second file
    implementation(libs.androidx.material.icons.extended)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // From second file

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.androidx.navigation.compose.v274)
    implementation(libs.accompanistNavigationAnimation)

    // Networking - Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.v4110)

    // Networking - Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.logback.classic)

    // Data & Serialization
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)

    // Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.accompanist.permissions)


    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Browser
    implementation(libs.androidx.browser)

    // Image Loading
    implementation(libs.coil.compose)

    // UI Enhancements
    implementation(libs.androidx.palette)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.animation)

    // Parsing
    implementation(libs.jsoup)
    implementation(libs.protolite.well.known.types)

    //qr code
    //implementation(libs.qrcode.kotlin.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}