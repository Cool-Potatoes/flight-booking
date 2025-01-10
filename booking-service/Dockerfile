FROM openjdk:17-jdk-slim
# Debian/Ubuntu
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY build/libs/booking-service-*.jar app.jar
EXPOSE 8120
ENTRYPOINT ["java", "-jar", "app.jar"]