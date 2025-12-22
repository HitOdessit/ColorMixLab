plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.colormixlab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.colormixlab"
        minSdk = 24
        targetSdk = 34
        versionCode = 16  // Auto-increment this with each commit
        versionName = "1.${versionCode}"  // Major.Minor format

        vectorDrawables {
            useSupportLibrary = true
        }

        // Make version available to the app
        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")
        buildConfigField("int", "MAJOR_VERSION", "1")
        buildConfigField("int", "MINOR_VERSION", "${versionCode}")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
}

