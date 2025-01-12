FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/gateway-service-*.jar app.jar

# Gateway 포트
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
