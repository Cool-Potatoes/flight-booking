FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# common.jar 복사를 제거
COPY build/libs/gateway-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
