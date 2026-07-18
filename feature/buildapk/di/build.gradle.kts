plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.buildapk.di"
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
    api(project(":feature:buildapk:api"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:github:api"))
    implementation(project(":feature:projects:api"))
    implementation(project(":feature:buildapk:data"))
    implementation(project(":feature:buildapk:presentation"))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
