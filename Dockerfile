# Use a multi-stage build to reduce the final image size
FROM maven:3.8.6-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

# Create the final image
FROM openjdk:21-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]