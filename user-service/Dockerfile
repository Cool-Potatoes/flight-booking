FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/user-service-*.jar app.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "app.jar"]
