@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(prisma.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    api(prisma.kotlinx.datetime)
    implementation(prisma.kotlinx.serialization.json)
    implementation(prisma.kotlinx.coroutines.core)
}