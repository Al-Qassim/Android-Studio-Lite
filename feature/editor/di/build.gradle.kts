plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.editor.di"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 33
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":feature:editor:api"))
    implementation(project(":feature:editor:data"))
    implementation(project(":feature:editor:presentation"))
    implementation(project(":feature:files:api"))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
