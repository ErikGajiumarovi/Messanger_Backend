# Используем официальный образ JDK
FROM eclipse-temurin:17-jdk

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файлы проекта
COPY . .

# Даем права на выполнение gradlew
RUN chmod +x ./gradlew

EXPOSE 8080

# Команда для запуска приложения
CMD ["./gradlew", "run"]