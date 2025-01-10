FROM openjdk:17-jdk-buster

# Debian/Ubuntu
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        # nftables \  (주석 처리)
        && rm -rf /var/lib/apt/lists/*

# # nf_tables 설정 (주석 처리)
# RUN nft add rule ip filter input tcp dport 8761 accept  # 8761 포트(Eureka Server) 열기
# RUN nft add rule ip filter input drop  # 다른 모든 INPUT 트래픽 차단

WORKDIR /app
COPY build/libs/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]