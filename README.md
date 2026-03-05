# Explore With Me

Учебный backend-проект в формате **микросервисной системы** для сервиса афиши событий.

Проект состоит из:
- **ewm-service** — основной сервис (бизнес-логика, пользователи, события, подборки, заявки).
- **stats-service** — сервис сбора и агрегации статистики просмотров.
- **stats-client / stats-dto** — клиент и общие DTO для обмена между сервисами.

---

## 1. Что умеет система

`Explore With Me` покрывает три роли API:

### Публичная часть
- просмотр опубликованных событий;
- фильтрация по тексту, категории, диапазону дат, признаку платности;
- сортировка по дате события или количеству просмотров;
- просмотр подборок и категорий;
- получение детальной карточки события.

### Приватная часть (авторизованный пользователь)
- создание событий;
- редактирование своих событий;
- отправка заявок на участие в чужих событиях;
- просмотр и управление собственными заявками;
- управление заявками участников для своих событий.

### Административная часть
- управление пользователями;
- управление категориями;
- создание/редактирование подборок (compilations);
- модерация событий (публикация/отклонение).

### Статистика
- запись «хитов» (обращений) в stats-service;
- получение агрегированной статистики по URI и временному интервалу;
- использование статистики при выдаче событий.

---

## 2. Технологический стек

- **Java 21**
- **Spring Boot 3** (Web, Validation, Data JPA, Security)
- **Spring Data JPA / Hibernate**
- **PostgreSQL**
- **Maven** (multi-module)
- **Lombok**
- **Docker / Docker Compose**
- Инструменты качества: **Checkstyle**, **SpotBugs**, **JaCoCo**

---

## 3. Архитектура и структура репозитория

```text
.
├── ewm-service/                  # основной сервис
│   ├── src/main/java/ru/practicum/ewm
│   │   ├── category/             # категории
│   │   ├── compilation/          # подборки событий
│   │   ├── event/                # события + review
│   │   ├── request/              # заявки на участие
│   │   ├── user/                 # пользователи
│   │   ├── security/             # JWT-аутентификация/авторизация
│   │   └── exception/            # централизованная обработка ошибок
│   └── src/main/resources/
├── stats-parent/
│   ├── stats-service/            # сервис статистики
│   ├── stats-client/             # клиент для вызова stats-service
│   └── stats-dto/                # DTO для межсервисного контракта
├── docker-compose.yml
├── ewm-main-service-spec.json    # OpenAPI main-сервиса
└── ewm-stats-service-spec.json   # OpenAPI stats-сервиса
```

### Ключевая идея взаимодействия сервисов
1. Клиент вызывает публичные/приватные/админ-эндпоинты `ewm-service`.
2. `ewm-service` отправляет информацию о запросах в `stats-service` через `stats-client`.
3. `stats-service` хранит хиты и по запросу отдает агрегаты просмотров.
4. `ewm-service` использует агрегаты для ответов API.

---

## 4. Быстрый старт

## Требования

- JDK 21
- Maven 3.9+
- Docker + Docker Compose

Проверка окружения:

```bash
java -version
mvn -version
docker --version
docker compose version
```

### Вариант A — запуск в Docker Compose (рекомендуется)

1. Собрать jar-файлы:
```bash
mvn clean package -DskipTests
```

2. Поднять инфраструктуру:
```bash
docker compose up --build
```

3. Сервисы по умолчанию:
- main-service: `http://localhost:8080`
- stats-service: `http://localhost:9090`
- ewm-db: `localhost:5433`
- stats-db: `localhost:5432`

4. Остановить:
```bash
docker compose down
```

### Вариант B — локальный запуск из IDE

1. Поднимите две PostgreSQL базы с параметрами из файлов:
- `ewm-service/src/main/resources/application.properties`
- `stats-parent/stats-service/src/main/resources/application.properties`

2. Запустите сначала сервис статистики:
- `ru.practicum.ewm.StatsApp`

3. Затем основной сервис:
- `ru.practicum.ewm.EwmApp`

---

## 5. Конфигурация

Основные настройки лежат в:
- `ewm-service/src/main/resources/application.properties`
- `stats-parent/stats-service/src/main/resources/application.properties`

Там задаются:
- порт приложения;
- datasource URL/логин/пароль;
- параметры JPA/Hibernate;
- SQL-инициализация схемы.

При запуске в Docker используются переменные окружения из `docker-compose.yml`.

---

## 6. API и контракты

В репозитории лежат OpenAPI-контракты:
- `ewm-main-service-spec.json`
- `ewm-stats-service-spec.json`

Дополнительно есть Postman-коллекции:
- `postman/Test Explore With Me - Main service.postman_collection.json`
- `postman/-Explore with me- API статистика.postman_collection.json`

Рекомендуемый сценарий изучения API:
1. Импортировать OpenAPI в Postman/Insomnia.
2. Поднять сервисы через Docker Compose.
3. Прогнать запросы из коллекций.

---

## 7. Основные доменные сущности

- **User** — пользователь платформы.
- **Event** — событие, создаваемое пользователем.
- **Category** — категория события.
- **Compilation** — подборка событий для витрины.
- **ParticipationRequest** — заявка пользователя на участие в событии.
- **EventReview** — модерационная информация по событиям (внутренний workflow).
- **Hit** (stats-service) — факт обращения к URI.

---

## 8. Проверка качества и сборка

### Полная сборка
```bash
mvn clean verify
```

### Быстрая сборка без тестов
```bash
mvn clean package -DskipTests
```

### Запуск конкретного модуля
```bash
mvn -pl ewm-service -am spring-boot:run
mvn -pl stats-parent/stats-service -am spring-boot:run
```

---

## 9. Типовые проблемы

### Сервис не стартует из-за БД
Проверьте, что:
- база доступна по host/port;
- совпадают логин/пароль;
- создана нужная схема;
- нет конфликта портов 5432/5433.

### main-service не пишет статистику
Проверьте:
- запущен ли `stats-service`;
- корректен URL stats-сервиса в конфигурации main-service;
- сетевую связность контейнеров (при Docker).

### Ошибки валидации DTO
Это штатное поведение: API валидирует входящие поля и возвращает ошибку при некорректных данных.

---

## 10. Назначение проекта

Это **учебный проект** для отработки:
- проектирования REST API;
- микросервисного взаимодействия;
- слоистой архитектуры Spring-приложений;
- работы с транзакционной бизнес-логикой;
- практики сборки и деплоя в контейнерах.
