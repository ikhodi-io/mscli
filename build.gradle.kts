plugins {
    id("application")
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
}

group = "com.marketsidekick"
version = "0.0.1"

application {
    mainClass.set("com.marketsidekick.MainKt")
}
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("com.microsoft.playwright:playwright:1.48.0")
    implementation("io.ktor:ktor-server-core:3.0.1")
    implementation("io.ktor:ktor-server-netty:3.0.1")

    // optional support for rendering markdown in help messages
    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1")
}

tasks.test {
    useJUnitPlatform()
}
