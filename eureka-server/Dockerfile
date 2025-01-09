FROM openjdk:17-jdk-slim

# Debian/Ubuntu
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
