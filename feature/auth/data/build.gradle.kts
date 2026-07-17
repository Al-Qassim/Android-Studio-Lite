plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.robotopia.androidstudiolite.feature.auth.data"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")

        val githubOAuthClientId = rootProject.file("local.properties")
            .takeIf { it.exists() }
            ?.readLines()
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("github.oauth.clientId=") && !it.startsWith("#") }
            ?.substringAfter("=", missingDelimiterValue = "")
            ?.trim()
            .orEmpty()
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
