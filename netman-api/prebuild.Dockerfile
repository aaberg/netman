FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache wget

# Set working directory
WORKDIR /app

COPY ./build/libs/netman-api-0.1-SNAPSHOT-all.jar app.jar

EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
