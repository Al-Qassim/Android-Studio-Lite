plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.files.di"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 33
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":feature:files:api"))
    implementation(project(":feature:files:data"))
    implementation(project(":feature:files:presentation"))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
