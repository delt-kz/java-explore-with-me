# Explore With Me Frontend

Фронтенд для backend-проекта `Explore With Me`.

Реализованы API:
- публичная афиша событий;
- фильтрация и переход в карточку события;
- регистрация и вход;
- личный кабинет;
- создание события;
- просмотр и модерация заявок;
- просмотр своих заявок на участие.

## Что уже есть

- отдельный dev-сервер на Vite;
- отдельная production-сборка;
- Dockerfile для развёртывания как отдельного статического сервера.

## Запуск

Из папки `frontend`:

```bash
npm install
npm run dev
```

По умолчанию фронт поднимется на:

```text
http://localhost:5173
```

## Адрес backend API

По умолчанию фронт ходит в:

```text
http://localhost:8080
```

Если нужно поменять адрес:

1. создать `.env`
2. положить туда:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Production preview

```bash
npm run build
npm run preview
```

Preview-сервер поднимется на:

```text
http://localhost:4173
```

## Docker

Сборка:

```bash
docker build -t ewm-frontend .
```

Запуск:

```bash
docker run --rm -p 8081:80 ewm-frontend
```

Тогда фронт будет доступен по адресу:

```text
http://localhost:8081
```

Если нужно передать другой адрес API на этапе сборки:

```bash
docker build --build-arg VITE_API_BASE_URL=http://localhost:8080 -t ewm-frontend .
```

### Docker Compose

Если нужен самый короткий запуск через готовый compose-файл из папки `frontend`:

```bash
docker compose up --build
```

Тогда фронт будет доступен по адресу:

```text
http://localhost
```
