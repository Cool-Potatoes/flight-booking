# 베이스 이미지 설정: OpenJDK 17 사용
FROM openjdk:17-jdk-slim

# 애플리케이션 작업 디렉토리 생성
WORKDIR /app

# Gradle 빌드 후 생성된 JAR 파일을 이미지에 복사
COPY build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트 설정 (Spring Boot 기본 포트 8160)
EXPOSE 8160

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
