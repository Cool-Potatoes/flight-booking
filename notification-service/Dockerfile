FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build/libs/common-*.jar common.jar
COPY build/libs/notification-service-*.jar app.jar

EXPOSE 8160

ENTRYPOINT ["java", "-jar", "app.jar"]
