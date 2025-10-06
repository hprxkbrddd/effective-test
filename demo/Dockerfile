# Используем более новую версию Gradle
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app

# Копируем все файлы проекта
COPY . .

# Даем права на выполнение gradlew (если используется wrapper)
RUN chmod +x ./gradlew || true

# Очищаем кеш Gradle и собираем с оптимизацией
RUN gradle clean --no-daemon && \
    gradle build -x test --no-daemon --build-cache --parallel

# Финальный образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем директории
RUN mkdir -p logs config

# Копируем JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Создаем пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]