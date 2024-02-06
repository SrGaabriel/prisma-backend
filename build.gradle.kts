@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(prisma.plugins.kotlin.jvm)
    alias(prisma.plugins.kotlinx.serialization) apply true
}

subprojects {
    group = "io.github.prismaplatform"
    version = "1.0"
}

repositories {
    mavenCentral()
}