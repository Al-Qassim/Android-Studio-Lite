pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Android Studio Lite"
include(":app")
include(":designsystem")
include(":core:model")
include(":core:database")
include(":feature:projects:api")
include(":feature:projects:impl")
include(":feature:files:api")
include(":feature:files:impl")
include(":feature:editor:api")
include(":feature:editor:impl")
include(":feature:build:api")
include(":feature:build:impl")
include(":integration:ide")
