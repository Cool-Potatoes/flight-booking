FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/config-server-*.jar app.jar
COPY src/main/resources/config-repo /app/config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]