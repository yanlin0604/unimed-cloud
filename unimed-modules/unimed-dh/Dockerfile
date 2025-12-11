# 多阶段构建 - 构建阶段
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .
COPY src ./src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

# 添加应用用户
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p logs && chown -R appuser:appgroup logs

# 设置 JVM 参数
ENV JAVA_OPTS="-XX:+UseG1GC -Xmx512m -Xms256m"

USER appuser

EXPOSE 9205

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9205/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]