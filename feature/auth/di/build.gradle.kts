plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.auth.di"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":feature:auth:api"))
    implementation(project(":feature:auth:data"))
    implementation(project(":feature:auth:presentation"))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
