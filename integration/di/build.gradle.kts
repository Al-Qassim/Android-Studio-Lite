plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.integration.di"
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
    implementation(project(":feature:projects:di"))
    implementation(project(":feature:files:di"))
    implementation(project(":feature:editor:di"))
    implementation(project(":feature:buildapk:di"))
    implementation(project(":feature:auth:di"))
    implementation(project(":feature:github:di"))
    implementation(project(":integration:database"))
    implementation(libs.koin.android)
    implementation(libs.androidx.core.ktx)
}
