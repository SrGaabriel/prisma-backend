pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("prisma") {
            from(files("prisma.versions.toml"))
        }
    }
}

rootProject.name = "prisma"

include("common")
include("database")
include("webserver")