enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/")
        }
}

rootProject.name = "MusicApp"
include(":app")
include(":core")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:song:api")
include(":feature:song:impl")
include(":feature:review:api")
include(":feature:review:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
