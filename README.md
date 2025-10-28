# Explore With Me - Микросервисная архитектура

## 📋 Описание проекта

Explore With Me - это платформа для поиска и организации событий, построенная на микросервисной архитектуре. 

Проект разделен на несколько независимых сервисов, каждый из которых отвечает за свою область функциональности.

## 🏗️ Архитектура системы

### Микросервисы

| Сервис | Порт | Описание | База данных |
|--------|------|----------|-------------|
| **Gateway** | 8080 | API Gateway - единая точка входа | - |
| **Event Service** | 8081 | Управление событиями, категориями, подборками | ewm_event |
| **User Service** | 8082 | Управление пользователями | ewm_user |
| **Request Service** | 8083 | Управление заявками на участие | ewm_request |
| **Comment Service** | 8084 | Комментарии к событиям | ewm_comment |
| **Stats Service** | 9090 | Статистика просмотров | ewm_stats_db |

### Инфраструктурные компоненты

| Компонент | Порт | Описание |
|-----------|------|----------|
| **Discovery Server** | 8761 | Eureka Server для регистрации сервисов |
| **Config Server** | 8888 | Централизованная конфигурация |
| **PostgreSQL** | 5432 | Основная база данных |

## 🔄 Диаграмма архитектуры

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client App    │────│  API Gateway    │────│ Discovery Server│
│                 │    │   (Port 8080)   │    │   (Port 8761)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                │
                ┌───────────────┼───────────────┐
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼─────┐
        │Event Service │ │User Service │ │Request Svc│
        │  (Port 8081) │ │ (Port 8082) │ │(Port 8083)│
        └──────────────┘ └─────────────┘ └──────────┘
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼─────┐
        │Comment Svc   │ │Stats Service│ │Config Svc │
        │ (Port 8084)  │ │ (Port 9090) │ │(Port 8888)│
        └──────────────┘ └─────────────┘ └──────────┘
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼─────┐
        │   ewm_comment│ │ewm_stats_db │ │PostgreSQL │
        │   Database   │ │  Database   │ │Database  │
        └──────────────┘ └─────────────┘ └──────────┘
```

## 🚀 Быстрый старт

### Предварительные требования

- Docker и Docker Compose
- Java 21 (для локальной разработки)
- Maven 3.8+ (для локальной разработки)

### Запуск через Docker Compose

1. **Клонируйте репозиторий:**
```bash
git clone <repository-url>
cd java-plus-graduation
```

2. **Запустите все сервисы:**
```bash
docker-compose up -d
```

3. **Проверьте статус сервисов:**
```bash
docker-compose ps
```

4. **Проверьте логи (опционально):**
```bash
docker-compose logs -f gateway-server
```

### Доступ к сервисам

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:8888
- **Stats Service**: http://localhost:9090

## 📚 API Endpoints

### Основные эндпоинты через Gateway (порт 8080)

#### События
- `GET /events` - Получить список событий
- `GET /events/{id}` - Получить событие по ID
- `POST /users/{userId}/events` - Создать событие
- `PATCH /users/{userId}/events/{eventId}` - Обновить событие

#### Пользователи
- `GET /users` - Получить список пользователей
- `POST /users` - Создать пользователя
- `DELETE /users/{userId}` - Удалить пользователя

#### Заявки на участие
- `GET /users/{userId}/requests` - Получить заявки пользователя
- `POST /users/{userId}/requests` - Подать заявку на участие
- `PATCH /users/{userId}/requests/{requestId}/cancel` - Отменить заявку

#### Комментарии
- `GET /events/{eventId}/comments` - Получить комментарии к событию
- `POST /users/{userId}/comments` - Создать комментарий
- `PATCH /users/{userId}/comments/{commentId}` - Обновить комментарий

## 🔧 Конфигурация

### Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `EUREKA_URI` | URL Eureka Server | http://localhost:8761/eureka/ |
| `SPRING_DATASOURCE_URL` | URL базы данных | jdbc:postgresql://ewm-db:5432/... |
| `SPRING_DATASOURCE_USERNAME` | Имя пользователя БД | ewm_user |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | ewm_password |

### Настройка Resilience4j

Gateway настроен с механизмами устойчивости:

- **Circuit Breaker**: автоматическое отключение при сбоях
- **Retry**: повторные попытки с экспоненциальным backoff
- **Fallback**: резервные ответы при недоступности сервисов

## 🗄️ Базы данных

### Схемы данных

#### Event Service (ewm_event)
- `events` - события
- `categories` - категории
- `compilations` - подборки событий

#### User Service (ewm_user)
- `users` - пользователи

#### Request Service (ewm_request)
- `requests` - заявки на участие

#### Comment Service (ewm_comment)
- `comments` - комментарии к событиям

#### Stats Service (ewm_stats_db)
- `endpoint_hits` - статистика просмотров

## 🔍 Мониторинг и отладка

### Health Checks

Все сервисы имеют health checks:
- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`

### Логи

Просмотр логов конкретного сервиса:
```bash
docker-compose logs -f <service-name>
```

### Eureka Dashboard

Доступен по адресу: http://localhost:8761

## 🧪 Тестирование

### Тестирование устойчивости

1. Запустите все сервисы
2. Выполните Postman тесты
3. Останавливайте сервисы по одному:
```bash
docker-compose stop <service-name>
```
4. Проверьте, что система продолжает работать с fallback механизмами

### Postman коллекции

Используйте следующие коллекции для тестирования:
- Основной API (через Gateway на порту 8080)
- Статистика (прямо на порту 9090)

## 🛠️ Разработка

### Локальная разработка

1. **Запустите инфраструктуру:**
```bash
docker-compose up -d discovery-server config-server ewm-db
```

2. **Запустите микросервис локально:**
```bash
cd core/event-service
mvn spring-boot:run
```

### Структура проекта

```
├── core/                    # Микросервисы
│   ├── event-service/      # Управление событиями
│   ├── user-service/       # Управление пользователями
│   ├── request-service/    # Управление заявками
│   ├── comment-service/    # Комментарии
│   ├── stats-service/      # Статистика
│   └── interaction-api/    # Общие API
├── infra/                  # Инфраструктура
│   ├── gateway-server/     # API Gateway
│   ├── config-server/      # Конфигурация
│   └── discovery-server/   # Eureka Server
├── init-scripts/           # Скрипты инициализации БД
└── compose.yaml           # Docker Compose конфигурация
```

## 🔒 Безопасность

- Все внешние запросы проходят через API Gateway
- Внутренние сервисы недоступны извне
- Используется service discovery для внутренней коммуникации
- Настроены circuit breakers для предотвращения каскадных сбоев

## 📈 Масштабирование

### Горизонтальное масштабирование

Каждый микросервис может быть масштабирован независимо:

```yaml
# В docker-compose.yml
event-service:
  deploy:
    replicas: 3
```

### Мониторинг производительности

- Используйте `/actuator/metrics` для метрик
- Настройте Prometheus для сбора метрик
- Используйте Grafana для визуализации

## 🐛 Устранение неполадок

### Частые проблемы

1. **Сервис не запускается:**
   - Проверьте логи: `docker-compose logs <service-name>`
   - Убедитесь, что база данных доступна

2. **Gateway не маршрутизирует запросы:**
   - Проверьте регистрацию в Eureka: http://localhost:8761
   - Проверьте конфигурацию маршрутов

3. **Ошибки подключения к БД:**
   - Убедитесь, что PostgreSQL запущен
   - Проверьте переменные окружения

### Полезные команды

```bash
# Перезапуск всех сервисов
docker-compose restart

# Просмотр статуса
docker-compose ps

# Очистка и пересборка
docker-compose down
docker-compose up --build
```
При возникновении проблем:

1. Проверьте логи сервисов
2. Убедитесь, что все зависимости запущены
3. Проверьте конфигурацию в Config Server
4. Обратитесь к документации Spring Cloud

---

Для начала работы просто выполните `docker-compose up -d` и перейдите на http://localhost:8080
