plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.core.error"
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
    implementation(libs.androidx.core.ktx)
}
