plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.robotopia.androidstudiolite.integration.ide"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":feature:projects:api"))
    api(project(":feature:files:api"))
    api(project(":feature:editor:api"))
    api(project(":feature:buildapk:api"))
    implementation(project(":feature:projects:impl"))
    implementation(project(":feature:files:impl"))
    implementation(project(":feature:editor:impl"))
    implementation(project(":feature:buildapk:impl"))
    implementation(project(":core:database"))
    implementation(project(":designsystem"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.koin.android)
    implementation(libs.androidx.core.ktx)
}
