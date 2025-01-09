FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/ticket-service-*.jar app.jar

# Ticket Service 포트
EXPOSE 8140

ENTRYPOINT ["java", "-jar", "app.jar"]
