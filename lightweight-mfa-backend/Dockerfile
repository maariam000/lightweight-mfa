# -----------------------------
# 1. Build stage
# -----------------------------
FROM gradle:8.14.3-jdk17-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Make gradlew executable
RUN chmod +x gradlew

# Build the JAR (skip tests for faster build)
RUN ./gradlew clean build -x test

# -----------------------------
# 2. Runtime stage
# -----------------------------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "app.jar"]
