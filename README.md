# Explore With Me

Учебный проект сервиса афиши событий. В репозитории есть backend на Spring Boot и простой отдельный frontend, который работает поверх того же API.

## Состав проекта

- `backend/ewm-service` - основной сервис (пользователи, события, категории, подборки, заявки).
- `backend/stats-parent/stats-service` - сервис сбора и агрегации статистики просмотров.
- `backend/stats-parent/stats-client` и `backend/stats-parent/stats-dto` - клиент и DTO для межсервисного взаимодействия.
- `frontend` - отдельный клиент для просмотра событий, входа и личного кабинета.

## Что есть в текущей версии

- JWT-аутентификация и авторизация по ролям `USER`/`ADMIN`.
- Приватные endpoint'ы в формате `/users/me/...`.
- Swagger UI (`springdoc-openapi`) c поддержкой Bearer JWT.
- Конфигурация сервисов в `application.yml`.
- Отдельный frontend на `Vite + React + TypeScript`.
- Docker-конфигурация для backend и frontend.

## Возможности API

### Публичная часть

- просмотр опубликованных событий;
- фильтрация по тексту, категориям, датам и платности;
- сортировка по дате события или просмотрам;
- просмотр категорий и подборок.

### Приватная часть

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
- в ответе возвращается JWT-токен с claim `userId`.

## Технологии

- Java 21
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL
- Maven
- React + TypeScript + Vite
- Docker / Docker Compose

## Структура репозитория

```text
.
├── backend/
│   ├── ewm-service/
│   ├── postman/
│   └── stats-parent/
│       ├── stats-service/
│       ├── stats-client/
│       └── stats-dto/
├── frontend/
└── .github/workflows/
```

## Быстрый старт

Требования:

- JDK 21
- Maven 3.9+
- Node.js 20+
- Docker + Docker Compose

### Backend

Сборка:

```bash
cd backend
mvn clean package -DskipTests
```

Запуск в Docker:

```bash
cd backend
docker compose up --build
```

По умолчанию будут доступны:

- main-service: `http://localhost:8080`
- stats-service: `http://localhost:9090`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Frontend

Локальный запуск:

```bash
cd frontend
npm install
npm run dev
```

Frontend будет доступен на `http://localhost:5173` и по умолчанию обращается к API на `http://localhost:8080`.

Запуск в Docker:

```bash
cd frontend
docker compose up --build
```

В этом режиме frontend будет доступен на `http://localhost`.

## Конфигурация

Основные файлы:

- `backend/ewm-service/src/main/resources/application.yml`
- `backend/stats-parent/stats-service/src/main/resources/application.yml`
- `frontend/.env`

## Тесты и качество

Полная проверка backend:

```bash
cd backend
mvn clean verify
```

Примеры тестов в проекте:

- `ewm-service`: `CategoryServiceTest`, `CompilationServiceTest`, `EventServiceTest`, `EventServiceIntegrationTest`, `UserServiceTest`, `RequestServiceTest`
- `stats-service`: `StatisticsServiceTest`, `StatisticsServiceIntegrationTest`
- Postman: `backend/postman/ewm-main-service.json`, `backend/postman/ewm-stat-service.json`

## Назначение проекта

Проект учебный и предназначен для практики:

- проектирования REST API;
- микросервисного взаимодействия;
- авторизации на JWT и разграничения ролей;
- тестирования и CI-пайплайна.
