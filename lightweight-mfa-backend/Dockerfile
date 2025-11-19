# Use an official Java runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Build the app (skip tests for faster build)
RUN ./gradlew build -x test

# Expose the port your app runs on
EXPOSE 8080

# Run the jar file from the build/libs folder
CMD ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]
