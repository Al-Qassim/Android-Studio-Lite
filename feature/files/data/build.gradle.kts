plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.files.data"
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
    api(project(":feature:files:api"))
    api(project(":feature:files:model"))
    api(project(":core:error"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
