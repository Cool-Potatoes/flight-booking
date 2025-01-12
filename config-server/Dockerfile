FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul


RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl git locales language-pack-ko && \
    apt-get clean && rm -rf /var/lib/apt/lists/* && \
    locale-gen ko_KR.UTF-8 && \
    update-locale LANG=ko_KR.UTF-8

WORKDIR /app

COPY build/libs/config-server-*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
