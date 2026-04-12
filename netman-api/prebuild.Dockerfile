FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache wget

WORKDIR /app

COPY ./build/libs/*-all.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
