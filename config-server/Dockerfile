FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/config-server-*.jar app.jar

# Config Server 포트
EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
