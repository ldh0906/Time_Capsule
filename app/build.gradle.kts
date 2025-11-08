plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Room 컴파일러
}

android {
    namespace = "com.example.timecapsule"

    // ⬇️ 최소 35 이상으로 올리세요 (가능하면 36)
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.timecapsule"
        minSdk = 24

        // ⬇️ 선택이지만 맞춰주면 깔끔함. 당장 부담되면 34 유지해도 됨.
        targetSdk = 35

        versionCode = 1
        versionName = "0.1.0"

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
        debug { }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-Xcontext-receivers"
        )
    }

    buildFeatures { compose = true }

    // compose plugin(alias(libs.plugins.kotlin.compose))를 쓰고 있으므로
    // composeOptions는 생략해도 됩니다. 필요하면 아래 주석 해제하세요.
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.15"
    // }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

dependencies {
    // ✅ Compose BOM로 버전 정합성 맞추기
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3:1.3.0")

    // Activity/Navigation
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // ⚠️ core-ktx 1.15.0은 compileSdk 35+를 요구 → 위에서 compileSdk를 올렸으니 OK
    implementation("androidx.core:core-ktx:1.15.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    // Room
    val room = "2.6.1"
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
