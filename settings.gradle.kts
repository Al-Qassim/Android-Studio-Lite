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
include(":core:error")

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

include(":feature:auth:model")
include(":feature:auth:api")
include(":feature:auth:data")
include(":feature:auth:presentation")
include(":feature:auth:di")

include(":feature:settings:api")
include(":feature:settings:data")
include(":feature:settings:presentation")
include(":feature:settings:di")

include(":feature:github:api")
include(":feature:github:data")
include(":feature:github:di")

include(":feature:git:api")
include(":feature:git:data")
include(":feature:git:presentation")
include(":feature:git:di")

include(":feature:onboarding:api")
include(":feature:onboarding:data")
include(":feature:onboarding:presentation")
include(":feature:onboarding:di")
