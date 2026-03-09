# Explore With Me

Учебный backend-проект в формате микросервисной системы для сервиса афиши событий.

Проект состоит из:
- `ewm-service` - основной сервис (пользователи, события, категории, подборки, заявки).
- `stats-parent/stats-service` - сервис сбора и агрегации статистики просмотров.
- `stats-parent/stats-client` и `stats-parent/stats-dto` - клиент и DTO для межсервисного взаимодействия.

## Что актуально в текущей версии

- Добавлена JWT-аутентификация и авторизация по ролям `USER`/`ADMIN`.
- Приватные endpoint'ы переведены на формат `/users/me/...` (идентификатор пользователя берется из JWT).
- Добавлен Swagger UI (`springdoc-openapi`) c поддержкой Bearer JWT.
- Конфигурация сервисов переведена с `application.properties` на `application.yml`.
- Добавлены unit и integration тесты для `ewm-service` и `stats-service`.
- Настроен CI workflow (`.github/workflows/api-tests.yml`) с запуском `mvn verify`.

## Возможности API

### Публичная часть
- просмотр опубликованных событий;
- фильтрация по тексту, категориям, датам, платности;
- сортировка по дате события или просмотрам;
- просмотр категорий и подборок.

### Приватная часть (авторизованный пользователь)
- управление своими событиями: `GET/POST/PATCH /users/me/events`;
- работа с заявками на участие: `GET/POST/PATCH /users/me/requests`;
- управление заявками участников по своему событию: `/users/me/events/{eventId}/requests`.

### Административная часть
- управление пользователями: `/admin/users`;
- управление категориями: `/admin/categories`;
- управление подборками: `/admin/compilations`;
- модерация событий: `/admin/events`.

### Аутентификация
- регистрация: `POST /auth/register`;
- вход: `POST /auth/authenticate`;
- в ответе возвращается JWT-токен; в токен добавляется claim `userId`.

## Технологии

- Java 21
- Spring Boot 3 (Web, Validation, Data JPA, Security)
- Spring Security + JWT (`jjwt`)
- Springdoc OpenAPI / Swagger UI
- PostgreSQL
- Maven (multi-module)
- Docker / Docker Compose
- Checkstyle, SpotBugs, JaCoCo

## Структура репозитория

```text
.
├── ewm-service/
├── stats-parent/
│   ├── stats-service/
│   ├── stats-client/
│   └── stats-dto/
├── postman/
│   ├── ewm-main-service.json
│   └── ewm-stat-service.json
├── .github/workflows/api-tests.yml
└── docker-compose.yml
```

## Быстрый старт

Требования:
- JDK 21
- Maven 3.9+
- Docker + Docker Compose

Сборка:
```bash
mvn clean package -DskipTests
```

Запуск в Docker:
```bash
docker compose up --build
```

Сервисы по умолчанию:
- main-service: `http://localhost:8080`
- stats-service: `http://localhost:9090`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Остановка:
```bash
docker compose down
```

## Конфигурация

Основные файлы:
- `ewm-service/src/main/resources/application.yml`
- `stats-parent/stats-service/src/main/resources/application.yml`

Тестовые профили:
- `ewm-service/src/main/resources/application-test.yml`
- `stats-parent/stats-service/src/main/resources/application-test.yml`

## Тесты и качество

Полная проверка (как в CI):
```bash
mvn clean verify
```

Запуск только тестов:
```bash
mvn test
```

Примеры тестов в проекте:
- `ewm-service`: `CategoryServiceTest`, `CompilationServiceTest`, `EventServiceTest`, `EventServiceIntegrationTest`, `UserServiceTest`, `RequestServiceTest`.
- `stats-service`: `StatisticsServiceTest`, `StatisticsServiceIntegrationTest`.
- Postman: `postman/ewm-main-service.json`, `postman/ewm-stat-service.json`

## Назначение проекта

Проект учебный и предназначен для практики:
- проектирования REST API;
- микросервисного взаимодействия;
- авторизации на JWT и разграничения ролей;
- тестирования и CI-пайплайна.