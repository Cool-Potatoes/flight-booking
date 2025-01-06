FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/payment-service-*.jar app.jar
EXPOSE 8130
ENTRYPOINT ["java", "-jar", "app.jar"]
