FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/flight-service-*.jar app.jar

# Flight Service 포트
EXPOSE 8150

ENTRYPOINT ["java", "-jar", "app.jar"]
