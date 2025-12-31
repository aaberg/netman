plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.3.0"
    id("com.google.devtools.ksp") version "2.3.4"
    id("io.micronaut.application") version "4.6.1"
    id("com.gradleup.shadow") version "9.3.0"
    id("io.micronaut.aot") version "4.6.1"
}

version = System.getenv("APP_VERSION") ?: "0.1-SNAPSHOT"
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

    // OpenTelemetry dependencies
    implementation("io.micronaut.tracing:micronaut-tracing-opentelemetry")
    implementation("io.micronaut.tracing:micronaut-tracing-opentelemetry-http")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc")

    // Azure insights
    implementation("com.azure:azure-monitor-opentelemetry-autoconfigure:1.4.0")

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
    implementation("io.micronaut:micronaut-management")
    implementation("org.slf4j:jul-to-slf4j")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.nats:micronaut-nats")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("io.micronaut:micronaut-http-client")

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
    sourceCompatibility = JavaVersion.toVersion("25")
    targetCompatibility = JavaVersion.toVersion("25")
}
kotlin {
    jvmToolchain(25)
}


graalvmNative {
    toolchainDetection.set(false)
    binaries {
        named("main") {
            buildArgs.add("--initialize-at-build-time=io.micronaut.security.authentication.AuthenticationMode")
        }
    }
}

//graalvmNative.toolchainDetection = false

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




