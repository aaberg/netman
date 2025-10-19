plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.2.20"
    id("com.google.devtools.ksp") version "2.2.20-2.0.4"
    id("io.micronaut.application") version "4.5.4"
//    id("io.micronaut.test-resources") version "4.5.4"
    id("com.gradleup.shadow") version "8.3.9"
    id("io.micronaut.aot") version "4.5.4"
}

version = "0.1"
group = "netman.api"
val kotlinVersion= project.properties["kotlinVersion"]

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut.openapi:micronaut-openapi")
    ksp("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("io.micronaut.openapi:micronaut-openapi-annotations")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("org.slf4j:jul-to-slf4j")
    implementation("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("io.micronaut:micronaut-http-client")

//    testImplementation(platform("org.testcontainers:testcontaivernelandners-bom:1.21.3"))
//    testImplementation("org.testcontainers:postgresql")
//    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("com.marcinziolo:kotlin-wiremock:2.1.1")
    testImplementation("org.wiremock:wiremock-standalone:3.13.1")
    testImplementation("commons-codec:commons-codec:1.19.0")

}


application {
    mainClass = "netman.ApplicationKt"
}
java {
    sourceCompatibility = JavaVersion.toVersion("24")
    targetCompatibility = JavaVersion.toVersion("24")
}
kotlin {
    jvmToolchain(25)
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("netman.api.*", "netman.api.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "25"
}


