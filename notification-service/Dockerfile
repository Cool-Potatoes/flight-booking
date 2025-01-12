FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/notification-service-*.jar app.jar

# Notification Service 포트
EXPOSE 8160

ENTRYPOINT ["java", "-jar", "app.jar"]
