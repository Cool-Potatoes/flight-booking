FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/user-service-*.jar app.jar

# User Service 포트
EXPOSE 8100

ENTRYPOINT ["java", "-jar", "app.jar"]
