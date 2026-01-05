# VMS 后端微服务 Dockerfile
# 多阶段构建：编译 + 运行

# ==================== 构建阶段 ====================
FROM maven:3.9-eclipse-temurin-21 AS builder

ARG SERVICE_NAME

WORKDIR /build

# 复制整个项目（为了解决模块依赖）
COPY VMS-springcloud/ .

# 先构建公共模块
RUN mvn clean install -pl vms-common,vms-api -am -DskipTests -q

# 构建目标服务
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests -q

# ==================== 运行阶段 ====================
FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME

WORKDIR /app

# 复制 JAR 文件
COPY --from=builder /build/${SERVICE_NAME}/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs /app/media

# 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 暴露端口（根据服务不同）
EXPOSE 8080 8081 8082 8083 8084 8085

# 使用 shell 形式以支持环境变量
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar -Dspring.profiles.active=docker -Dlogging.file.path=/app/logs app.jar"]
