# Stage 1: Build the application
FROM gradle:latest AS build
WORKDIR /app

# Copy all project files into the container
COPY . .

# Build the project using Gradle
RUN gradle clean shadowJar --no-daemon

# Stage 2: Create the runtime container
FROM amazoncorretto:17
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port defined in Application.conf
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
