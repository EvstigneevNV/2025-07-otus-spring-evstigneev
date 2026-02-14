# syntax=docker/dockerfile:1

# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Кеш зависимостей
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Сборка
COPY src ./src
RUN mvn -q -DskipTests package

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# non-root
RUN useradd -r -u 1001 appuser
USER appuser

# кладём executable jar
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]