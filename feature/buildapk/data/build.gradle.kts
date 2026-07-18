plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.buildapk.data"
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
    api(project(":feature:buildapk:model"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:github:api"))
    implementation(project(":feature:projects:api"))
    implementation(project(":core:error"))
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
