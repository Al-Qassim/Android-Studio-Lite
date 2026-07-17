plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.robotopia.androidstudiolite.integration.navigation"
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
    api(project(":feature:projects:model"))
    api(project(":feature:files:api"))
    api(project(":feature:files:model"))
    api(project(":feature:editor:api"))
    api(project(":feature:editor:model"))
    api(project(":feature:buildapk:api"))
    api(project(":feature:settings:api"))
    api(project(":feature:onboarding:api"))
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}
