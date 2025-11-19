# Use an official Java runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Install dependencies needed for Gradle build
RUN apk add --no-cache bash git openssh

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Copy the source code
COPY src ./src

# Build the app (skip tests for faster build)
RUN ./gradlew build -x test

# Expose the port your app runs on
EXPOSE 8080

# Run the jar file from build/libs
CMD ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]
