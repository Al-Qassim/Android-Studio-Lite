plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.onboarding.di"
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
    api(project(":feature:onboarding:api"))
    implementation(project(":feature:onboarding:data"))
    implementation(project(":feature:onboarding:presentation"))
    implementation(project(":feature:auth:api"))
    implementation(libs.koin.android)
}
