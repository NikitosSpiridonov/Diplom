# Процедура запуска автотестов

## ПО для запуска автотестов

- IntelliJ IDEA Community Edition 2024.1.1 - интегрированная среда разработки
- Java Development Kit (JDK) Eclipse temurin-11.0.21
- Docker Desktop - платформа контейнеризации
- Браузер - Google Chrome

## Запуск программ

1. Запустить Docker Desktop и свернуть
2. Склонировать репозиторий https://github.com/NikitosSpiridonov/Diplom
3. Открыть его в IntelliJ IDEA
4. Открыть локальный терминал IntelliJ IDEA
5. В первой вкладке терминала ввести команду docker-compose up - запустится докер-контейнер

### Запуск приложения

1. Для тестирования запросов в БД MySQL запустите приложение aqa-shop.jar:

   java -DdbUrl=jdbc:mysql://localhost:3306/app -DdbUser=app -DdbPass=pass -jar "./artifacts/aqa-shop.jar"

2. Для тестирования запросов в БД PostgreSQL запустите приложение с указанными параметрами:

   java -DdbUrl=jdbc:postgresql://localhost:5432/db -DdbUser=app -DdbPass=pass -jar "./artifacts/aqa-shop.jar"

### Запуск автотестов

1. Для запуска автотестов с проверкой БД MySQL выполните команду:

   ./gradlew test

2. Для запуска автотестов с проверкой БД PostgreSQL выполните команду:

   ./gradlew -DdbUrl=jdbc:postgresql://localhost:5432/db -DdbUsername=app -DdbPassword=pass test

### Генерация отчетов Allure

1. После прохождения тестов в третьей вкладке терминала ввести команду .\gradlew allureServe- сгенерируется Allure отчет.

### Шаг 6: Завершения работы

1. Для завершения работы allureServe выполнить команду:

   Ctrl+C

2. Для остановки работы контейнеров выполнить команду:

   docker-compose down
