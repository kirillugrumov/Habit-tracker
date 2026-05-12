# Этап сборки (Maven + JDK)
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn -B -DskipTests package

# Копируем итоговый JAR в простой файл app.jar (удобно)
RUN JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)" \
    && cp "$JAR_FILE" app.jar

# Финальный образ (JRE + curl)
FROM eclipse-temurin:17-jre-jammy

# Устанавливаем curl для healthcheck (и обновляем систему)
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

# Создаём непривилегированного пользователя (как в примере не было, но добавим для безопасности)
RUN useradd -m -u 1001 spring && chown -R spring:spring /app
USER spring

ENV PORT=8080
EXPOSE ${PORT}

# Healthcheck через actuator (требует curl и проверку строки)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -fsS "http://localhost:${PORT}/actuator/health" | grep -q '"status":"UP"'

# Запуск: можно передать JAVA_OPTS и переопределить порт
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -Dserver.port=${PORT:-8080} -jar /app/app.jar"]