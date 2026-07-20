plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.git.di"
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
    api(project(":feature:git:api"))
    implementation(project(":feature:git:data"))
    implementation(project(":feature:git:presentation"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:github:api"))
    implementation(project(":feature:projects:api"))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
