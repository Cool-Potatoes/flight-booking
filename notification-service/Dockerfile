# Base image
FROM openjdk:17-jdk-slim

# Set environment variables
ENV APP_HOME=/app

# Create app directory
WORKDIR $APP_HOME

# Copy build artifacts
COPY build/libs/notification-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the server port
EXPOSE 8160

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
