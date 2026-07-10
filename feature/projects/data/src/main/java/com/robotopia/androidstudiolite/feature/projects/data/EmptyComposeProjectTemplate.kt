package com.robotopia.androidstudiolite.feature.projects.data

import java.io.File

/**
 * Writes a standard empty-Compose Android project tree under [projectRoot].
 * Includes Gradle skeleton + wrapper properties (no wrapper jars), manifest,
 * MainActivity + theme, and basic res — per v0.1 plan decision #16.
 */
internal object EmptyComposeProjectTemplate {

    fun write(projectRoot: File, projectName: String, packageName: String) {
        projectRoot.mkdirs()

        writeText(projectRoot, ".gitignore", GITIGNORE)
        writeText(projectRoot, "settings.gradle.kts", settingsGradle(projectName))
        writeText(projectRoot, "build.gradle.kts", ROOT_BUILD_GRADLE)
        writeText(projectRoot, "gradle.properties", GRADLE_PROPERTIES)
        writeText(
            projectRoot,
            "gradle/wrapper/gradle-wrapper.properties",
            GRADLE_WRAPPER_PROPERTIES,
        )

        writeText(projectRoot, "app/build.gradle.kts", appBuildGradle(packageName))
        writeText(projectRoot, "app/src/main/AndroidManifest.xml", ANDROID_MANIFEST)

        val packagePath = packageName.replace('.', '/')
        writeText(
            projectRoot,
            "app/src/main/java/$packagePath/MainActivity.kt",
            mainActivity(packageName),
        )
        writeText(
            projectRoot,
            "app/src/main/java/$packagePath/ui/theme/Color.kt",
            themeColor(packageName),
        )
        writeText(
            projectRoot,
            "app/src/main/java/$packagePath/ui/theme/Theme.kt",
            themeTheme(packageName),
        )
        writeText(
            projectRoot,
            "app/src/main/java/$packagePath/ui/theme/Type.kt",
            themeType(packageName),
        )

        writeText(projectRoot, "app/src/main/res/values/strings.xml", stringsXml(projectName))
        writeText(projectRoot, "app/src/main/res/values/colors.xml", COLORS_XML)
        writeText(projectRoot, "app/src/main/res/values/themes.xml", THEMES_XML)
        writeText(projectRoot, "app/src/main/res/xml/backup_rules.xml", BACKUP_RULES)
        writeText(
            projectRoot,
            "app/src/main/res/xml/data_extraction_rules.xml",
            DATA_EXTRACTION_RULES,
        )
        writeText(
            projectRoot,
            "app/src/main/res/drawable/ic_launcher_foreground.xml",
            IC_LAUNCHER_FOREGROUND,
        )
        writeText(
            projectRoot,
            "app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml",
            IC_LAUNCHER,
        )
        writeText(
            projectRoot,
            "app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml",
            IC_LAUNCHER,
        )
    }

    private fun writeText(root: File, relativePath: String, content: String) {
        val file = File(root, relativePath)
        file.parentFile?.mkdirs()
        file.writeText(content.trimIndent() + "\n")
    }

    private const val GITIGNORE = """
        *.iml
        .gradle
        /local.properties
        /.idea
        .DS_Store
        /build
        /captures
        .externalNativeBuild
        .cxx
        app/build
    """

    private fun settingsGradle(projectName: String): String = """
        pluginManagement {
            repositories {
                google()
                mavenCentral()
                gradlePluginPortal()
            }
        }
        dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {
                google()
                mavenCentral()
            }
        }

        rootProject.name = "$projectName"
        include(":app")
    """

    private const val ROOT_BUILD_GRADLE = """
        plugins {
            id("com.android.application") version "8.7.3" apply false
            id("org.jetbrains.kotlin.android") version "2.0.21" apply false
            id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
        }
    """

    private const val GRADLE_PROPERTIES = """
        org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
        android.useAndroidX=true
        kotlin.code.style=official
        android.nonTransitiveRClass=true
    """

    private const val GRADLE_WRAPPER_PROPERTIES = """
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        distributionUrl=https\://services.gradle.org/distributions/gradle-8.11.1-bin.zip
        networkTimeout=10000
        validateDistributionUrl=true
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists
    """

    private fun appBuildGradle(packageName: String): String = """
        plugins {
            id("com.android.application")
            id("org.jetbrains.kotlin.android")
            id("org.jetbrains.kotlin.plugin.compose")
        }

        android {
            namespace = "$packageName"
            compileSdk = 35

            defaultConfig {
                applicationId = "$packageName"
                minSdk = 26
                targetSdk = 35
                versionCode = 1
                versionName = "1.0"
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
            kotlinOptions {
                jvmTarget = "11"
            }
            buildFeatures {
                compose = true
            }
        }

        dependencies {
            val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
            implementation(composeBom)
            implementation("androidx.compose.ui:ui")
            implementation("androidx.compose.ui:ui-tooling-preview")
            implementation("androidx.compose.material3:material3")
            implementation("androidx.activity:activity-compose:1.9.3")
            implementation("androidx.core:core-ktx:1.15.0")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
            debugImplementation("androidx.compose.ui:ui-tooling")
        }
    """

    private const val ANDROID_MANIFEST = """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android">

            <application
                android:allowBackup="true"
                android:icon="@mipmap/ic_launcher"
                android:label="@string/app_name"
                android:roundIcon="@mipmap/ic_launcher_round"
                android:supportsRtl="true"
                android:theme="@style/Theme.EmptyCompose">
                <activity
                    android:name=".MainActivity"
                    android:exported="true"
                    android:theme="@style/Theme.EmptyCompose">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
                </activity>
            </application>

        </manifest>
    """

    private fun mainActivity(packageName: String): String = """
        package $packageName

        import android.os.Bundle
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.setContent
        import androidx.activity.enableEdgeToEdge
        import androidx.compose.foundation.layout.fillMaxSize
        import androidx.compose.foundation.layout.padding
        import androidx.compose.material3.Scaffold
        import androidx.compose.material3.Text
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.tooling.preview.Preview
        import $packageName.ui.theme.EmptyComposeTheme

        class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                enableEdgeToEdge()
                setContent {
                    EmptyComposeTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun Greeting(name: String, modifier: Modifier = Modifier) {
            Text(
                text = "Hello ${'$'}name!",
                modifier = modifier,
            )
        }

        @Preview(showBackground = true)
        @Composable
        fun GreetingPreview() {
            EmptyComposeTheme {
                Greeting("Android")
            }
        }
    """

    private fun themeColor(packageName: String): String = """
        package $packageName.ui.theme

        import androidx.compose.ui.graphics.Color

        val Purple80 = Color(0xFFD0BCFF)
        val PurpleGrey80 = Color(0xFFCCC2DC)
        val Pink80 = Color(0xFFEFB8C8)

        val Purple40 = Color(0xFF6650a4)
        val PurpleGrey40 = Color(0xFF625b71)
        val Pink40 = Color(0xFF7D5260)
    """

    private fun themeTheme(packageName: String): String = """
        package $packageName.ui.theme

        import androidx.compose.foundation.isSystemInDarkTheme
        import androidx.compose.material3.MaterialTheme
        import androidx.compose.material3.darkColorScheme
        import androidx.compose.material3.lightColorScheme
        import androidx.compose.runtime.Composable

        private val DarkColorScheme = darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80,
        )

        private val LightColorScheme = lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40,
        )

        @Composable
        fun EmptyComposeTheme(
            darkTheme: Boolean = isSystemInDarkTheme(),
            content: @Composable () -> Unit,
        ) {
            val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content,
            )
        }
    """

    private fun themeType(packageName: String): String = """
        package $packageName.ui.theme

        import androidx.compose.material3.Typography
        import androidx.compose.ui.text.TextStyle
        import androidx.compose.ui.text.font.FontFamily
        import androidx.compose.ui.text.font.FontWeight
        import androidx.compose.ui.unit.sp

        val Typography = Typography(
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        )
    """

    private fun stringsXml(projectName: String): String = """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <string name="app_name">$projectName</string>
        </resources>
    """

    private const val COLORS_XML = """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <color name="ic_launcher_background">#3DDC84</color>
        </resources>
    """

    private const val THEMES_XML = """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <style name="Theme.EmptyCompose" parent="android:Theme.Material.Light.NoActionBar" />
        </resources>
    """

    private const val BACKUP_RULES = """
        <?xml version="1.0" encoding="utf-8"?>
        <full-backup-content />
    """

    private const val DATA_EXTRACTION_RULES = """
        <?xml version="1.0" encoding="utf-8"?>
        <data-extraction-rules>
            <cloud-backup />
            <device-transfer />
        </data-extraction-rules>
    """

    private const val IC_LAUNCHER_FOREGROUND = """
        <?xml version="1.0" encoding="utf-8"?>
        <vector xmlns:android="http://schemas.android.com/apk/res/android"
            android:width="108dp"
            android:height="108dp"
            android:viewportWidth="108"
            android:viewportHeight="108">
            <path
                android:fillColor="#3DDC84"
                android:pathData="M0,0h108v108h-108z" />
            <path
                android:fillColor="#00000000"
                android:pathData="M9,0L9,108L0,108L0,0L9,0ZM19,0L19,108L10,108L10,0L19,0Z" />
        </vector>
    """

    private const val IC_LAUNCHER = """
        <?xml version="1.0" encoding="utf-8"?>
        <adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
            <background android:drawable="@color/ic_launcher_background" />
            <foreground android:drawable="@drawable/ic_launcher_foreground" />
        </adaptive-icon>
    """
}
