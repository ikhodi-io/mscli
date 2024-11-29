plugins {
    id("application")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.marketsidekick"
version = "0.0.1"

application {
    mainClass.set("com.marketsidekick.MainKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1")// optional support for rendering markdown in help messages
    implementation("com.microsoft.playwright:playwright:1.48.0")
    implementation(libs.ktor.client.content.negotiation) //used for request serialization
    implementation(libs.ktor.server.content.negotiation) //used for response serialization
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation("io.ktor:ktor-serialization-gson-jvm:3.0.1")
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`  // Connect standard input (stdin) to the console input
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.test {
    useJUnitPlatform()
}