plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.settings.di"
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
    api(project(":feature:settings:api"))
    implementation(project(":feature:settings:presentation"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:buildapk:api"))
    implementation(libs.koin.android)
}
