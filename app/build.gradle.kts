plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Room 컴파일러
}

android {
    namespace = "com.example.timecapsule"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.timecapsule"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        // Room에서 Java 8+ API 쓰기 위한 설정 (Compose도 문제 없음)
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // 필요 시 debug 전용 설정
        }
    }

    compileOptions {
        // JDK 17 권장
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        // 코루틴 + Compose 빌드 안정성 향상
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xcontext-receivers"
        )
    }

    buildFeatures { compose = true }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    implementation(libs.androidx.compose.runtime)
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Navigation-Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Lifecycle / ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    // Room
    val room = "2.6.1"
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    // Kotlin coroutines (선택 — 최신 gradle 플러그인/compose는 내장된 경우도 많음)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
