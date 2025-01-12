# Debian 기반 OpenJDK 이미지 사용
FROM openjdk:17-jdk-slim-buster

# 시스템 패키지 업데이트 및 curl 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/eureka-server-*.jar app.jar

# Eureka Server 포트 노출
EXPOSE 8761

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]