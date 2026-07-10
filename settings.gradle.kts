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

include(":feature:projects:model")
include(":feature:projects:api")
include(":feature:projects:data")
include(":feature:projects:presentation")
include(":feature:projects:di")

include(":feature:files:model")
include(":feature:files:api")
include(":feature:files:data")
include(":feature:files:presentation")
include(":feature:files:di")

include(":feature:editor:model")
include(":feature:editor:api")
include(":feature:editor:data")
include(":feature:editor:presentation")
include(":feature:editor:di")

include(":feature:buildapk:model")
include(":feature:buildapk:api")
include(":feature:buildapk:data")
include(":feature:buildapk:presentation")
include(":feature:buildapk:di")

include(":integration:database")
include(":integration:di")
include(":integration:navigation")
