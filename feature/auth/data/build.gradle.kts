plugins {
    alias(libs.plugins.android.library)
}

import java.util.Properties

android {
    namespace = "com.robotopia.androidstudiolite.feature.auth.data"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 34
        consumerProguardFiles("consumer-rules.pro")

        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        val githubOAuthClientId = localProperties.getProperty("github.oauth.clientId").orEmpty()
        buildConfigField(
            "String",
            "GITHUB_OAUTH_CLIENT_ID",
            "\"${githubOAuthClientId.replace("\\", "\\\\").replace("\"", "\\\"")}\"",
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(project(":feature:auth:api"))
    api(project(":feature:auth:model"))
    api(project(":feature:github:api"))
    implementation(project(":core:error"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
