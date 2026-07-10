plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.editor.data"
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
    api(project(":feature:editor:api"))
    api(project(":feature:editor:model"))
    api(project(":feature:files:model"))
    implementation(libs.kotlinx.coroutines.core)
}
