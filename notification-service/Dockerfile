# Base image
FROM openjdk:17-jdk-slim

# Set environment variables
ENV APP_HOME=/app
ENV SERVER_PORT=8160

# Create app directory
WORKDIR $APP_HOME

# Copy build artifacts
COPY build/libs/notification-service-*.jar app.jar

# Expose the server port
EXPOSE $SERVER_PORT

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s CMD curl -f http://localhost:$SERVER_PORT/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
