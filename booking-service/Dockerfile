FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/booking-service-*.jar app.jar
EXPOSE 8120
ENTRYPOINT ["java", "-jar", "app.jar"]
