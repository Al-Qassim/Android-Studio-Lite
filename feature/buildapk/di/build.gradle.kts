plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.buildapk.di"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 34
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":feature:buildapk:api"))
    implementation(project(":feature:buildapk:data"))
    implementation(project(":feature:buildapk:presentation"))
    implementation(libs.koin.android)
}
