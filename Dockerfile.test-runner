FROM maven:3.8.5-openjdk-17

WORKDIR /usr/src/app

# Установка зависимостей для CI
RUN apt-get update && apt-get install -y \
    curl \
    docker-compose \
    && rm -rf /var/lib/apt/lists/*

# Копирование pom.xml
COPY pom.xml .

# Установка зависимостей
RUN mvn dependency:go-offline -B

# Копирование исходного кода
COPY src ./src

# Установка скрипта для запуска
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]