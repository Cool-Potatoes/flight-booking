FROM ubuntu:20.04

ENV APP_HOME=/app
ENV SERVER_PORT=8160

WORKDIR $APP_HOME

# 자주 변경되지 않는 파일들을 먼저 복사 (예: 설정 파일, 리소스 파일)
# COPY [자주 변경되지 않는 파일들] $APP_HOME

COPY build/libs/notification-service-*.jar app.jar

RUN apt-get update && \
    apt-get install -y curl && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*  # 캐시 제거하여 이미지 크기 최소화

EXPOSE $SERVER_PORT

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s CMD curl -f http://localhost:$SERVER_PORT/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]