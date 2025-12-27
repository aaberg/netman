# Multi-stage build for Micronaut application
# Stage 1: Build the application
FROM eclipse-temurin:25-jdk-alpine AS builder

# Set working directory
WORKDIR /home/app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY micronaut-cli.yml .

# Copy source code
COPY src src

# Make gradlew executable and build the application
RUN chmod +x gradlew && ./gradlew buildLayers --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

# Set working directory
WORKDIR /app

# Copy application layers from builder
COPY --from=builder /home/app/build/layers/libs /app/libs
COPY --from=builder /home/app/build/layers/classes /app/classes
COPY --from=builder /home/app/build/layers/resources /app/resources
COPY --from=builder /home/app/build/layers/application.jar /app/application.jar

# Expose the application port
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
