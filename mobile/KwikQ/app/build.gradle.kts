plugins {
    alias(libs.plugins.android.application)
}

val emulatorBaseUrl = (project.findProperty("EMULATOR_BASE_URL") as String?)
    ?: (project.findProperty("DEBUG_BASE_URL") as String?)
    ?: "http://10.0.2.2:8080/"
val deviceBaseUrl = (project.findProperty("DEVICE_BASE_URL") as String?) ?: "http://192.168.1.100:8080/"
val releaseBaseUrl = (project.findProperty("RELEASE_BASE_URL") as String?) ?: "https://api.example.com/"

android {
    namespace = "com.example.kwikq"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.kwikq"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "backendTarget"
    productFlavors {
        create("emulator") {
            dimension = "backendTarget"
            buildConfigField("String", "BASE_URL", "\"$emulatorBaseUrl\"")
        }
        create("device") {
            dimension = "backendTarget"
            buildConfigField("String", "BASE_URL", "\"$deviceBaseUrl\"")
        }
        create("prod") {
            dimension = "backendTarget"
            buildConfigField("String", "BASE_URL", "\"$releaseBaseUrl\"")
        }
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    
    // OkHttp - for JWT interceptor
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}