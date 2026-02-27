# BookVerse - проектная работа (онлайн-библиотека)

Тема: **онлайн-библиотека** в микросервисной архитектуре.

## Сервисы

- **api-gateway** (порт 8080) - API Gateway (Spring Cloud Gateway) + CircuitBreaker (Resilience4j)
- **auth-service** (порт 8081) - регистрация/логин, выпуск JWT, публикация JWKSet
- **library-service** (порт 8082) - каталог книг и выдача/возврат, JWT Resource Server, JPA/Flyway, Cache, Retry/CircuitBreaker
- **notification-service** (порт 8083) - потребитель событий RabbitMQ (выдача/возврат)

Коммуникации:
- REST через **API Gateway**
- события выдачи/возврата через **RabbitMQ (TopicExchange)**

## Запуск

Требования: Docker + Docker Compose.

```bash
docker-compose up --build
```

После старта:
- Gateway: http://localhost:8080
- RabbitMQ UI: http://localhost:15672 (логин/пароль: guest/guest)

## Данные по умолчанию

В `auth-service` автоматически создаётся админ:
- email: `admin@bookverse.local`
- password: `admin123`
- roles: `USER,ADMIN`

В `library-service` при пустой БД добавляются 2 книги: **Clean Code**, **Effective Java**.

## Примеры запросов (через gateway)

### 1) Логин (получить JWT)

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@bookverse.local","password":"admin123"}'
```

Сохрани `accessToken`.

### 2) Список книг

```bash
TOKEN="<accessToken>"
curl -s http://localhost:8080/api/books \
  -H "Authorization: Bearer $TOKEN"
```

### 3) Добавить книгу (только ADMIN)

```bash
TOKEN="<accessToken>"
curl -s -X POST http://localhost:8080/api/books \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Domain-Driven Design","author":"Eric Evans","publishYear":2003,"isbn":"9780321125217","totalCopies":2}'
```

### 4) Взять книгу

```bash
TOKEN="<accessToken>"
BOOK_ID="<uuid>"
curl -s -X POST http://localhost:8080/api/loans/$BOOK_ID \
  -H "Authorization: Bearer $TOKEN"
```

### 5) Вернуть книгу

```bash
TOKEN="<accessToken>"
BOOK_ID="<uuid>"
curl -s -X POST http://localhost:8080/api/loans/$BOOK_ID/return \
  -H "Authorization: Bearer $TOKEN"
```

### 6) Посмотреть события (notification-service)

```bash
curl -s http://localhost:8080/api/notifications
```

## Технологии

- Java 17
- Spring Boot 3.5.5
- Spring MVC
- Spring Security + JWT (Resource Server)
- Spring Data JPA + Flyway
- RabbitMQ (AMQP)
- Resilience4j (CircuitBreaker/Retry)
- Cache (Caffeine)
- Docker + docker-compose

