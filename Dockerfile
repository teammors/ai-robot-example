# 多阶段构建：构建阶段
FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 运行阶段
FROM amazoncorretto:21-alpine
RUN apk add --no-cache curl

# 创建非root用户运行应用
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app
COPY --from=builder /app/target/*.jar robot-example.jar

EXPOSE 8900

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8900/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/robot-example.jar"]