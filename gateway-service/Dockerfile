# Ubuntu 기반 OpenJDK 이미지 사용
FROM ubuntu:20.04

# 시스템 패키지 업데이트 및 curl 설치
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*  # 캐시 제거하여 이미지 크기 최소화
WORKDIR /app
COPY build/libs/gateway-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
