plugins {
    kotlin("jvm")
    alias(prisma.plugins.kotlinx.serialization) apply true
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(project(":database"))
    implementation(prisma.bundles.ktor.server)
    implementation(prisma.bundles.koin)
    implementation(prisma.bundles.logging)
    implementation(prisma.bcrypt)
    runtimeOnly(prisma.postgresql)
}