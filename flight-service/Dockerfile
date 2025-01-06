FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/flight-service-*.jar app.jar
EXPOSE 8150
ENTRYPOINT ["java", "-jar", "app.jar"]
