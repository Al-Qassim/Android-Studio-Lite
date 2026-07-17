plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.projects.di"
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
    api(project(":feature:projects:api"))
    implementation(project(":feature:projects:data"))
    implementation(project(":feature:projects:presentation"))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
