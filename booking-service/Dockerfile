FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY build/libs/booking-service-*.jar app.jar

# Booking Service 포트
EXPOSE 8120

ENTRYPOINT ["java", "-jar", "app.jar"]
