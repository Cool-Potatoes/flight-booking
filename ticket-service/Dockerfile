FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/ticket-service-*.jar app.jar
EXPOSE 8140
ENTRYPOINT ["java", "-jar", "app.jar"]
