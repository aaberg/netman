# Multi-stage build for Micronaut native image
FROM ghcr.io/graalvm/native-image-community:25-muslib AS builder

# Install required build tools
RUN microdnf install -y findutils

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

# Build the native image
RUN ./gradlew nativeCompile --no-daemon --no-configuration-cache

# Runtime stage
FROM cgr.dev/chainguard/glibc-dynamic:latest

# Expose the application port
EXPOSE 8080

# Copy the native binary from builder
COPY --from=builder /home/app/build/native/nativeCompile/netman-api /app/netman-api

# Set the entrypoint
ENTRYPOINT ["/app/netman-api"]
