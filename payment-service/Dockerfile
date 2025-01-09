FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/payment-service-*.jar app.jar

# Payment Service 포트
EXPOSE 8130

ENTRYPOINT ["java", "-jar", "app.jar"]
