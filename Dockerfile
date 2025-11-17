FROM maven:3.8.5-openjdk-17

WORKDIR /usr/src/app

# Копирование pom.xml
COPY pom.xml .

# Установка зависимостей
RUN mvn dependency:go-offline -B

# Копирование исходного кода
COPY src ./src

# Установка скрипта для запуска
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

# Запуск тестов напрямую
CMD ["mvn", "test"]