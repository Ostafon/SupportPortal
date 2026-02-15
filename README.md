# 🎫 Support Portal

> Enterprise-grade Support Ticket Management System built with Spring Boot

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java Version](https://img.shields.io/badge/Java-17-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-green)]()
[![License](https://img.shields.io/badge/license-MIT-blue)]()

## 📋 Описание

Support Portal - это современная система управления тикетами поддержки с полным функционалом:
- 🔐 JWT аутентификация и авторизация
- 🎫 Управление тикетами
- 💬 Чат система
- 📚 База знаний
- 🔔 Система уведомлений
- 👥 Управление пользователями и группами

## ✨ Что нового (версия 1.0.0)

### 🔥 Критические исправления
- ✅ Исправлены 8 критических ошибок безопасности
- ✅ Улучшена обработка JWT токенов
- ✅ Исправлена работа с authorities в Spring Security
- ✅ Добавлена проверка активности пользователей

### 🎯 Новые возможности
- ✅ Swagger UI документация API
- ✅ Автоматический аудит изменений
- ✅ Кастомная валидация паролей
- ✅ Generic API response обертки
- ✅ Улучшенное логирование

### 📦 Обновления зависимостей
- JWT: 0.11.5 → 0.12.5
- Lombok: 1.18.28 → 1.18.36
- Spring Boot: 4.0.2

## 🚀 Быстрый старт

### Требования
- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### Установка

1. **Клонируйте репозиторий**
```bash
git clone <repository-url>
cd SupportPortal
```

2. **Настройте базу данных**
```sql
CREATE DATABASE supportdb;
CREATE USER ostafon WITH PASSWORD '0000';
GRANT ALL PRIVILEGES ON DATABASE supportdb TO ostafon;
```

3. **Установите переменные окружения**

Windows (PowerShell):
```powershell
```

4. **Запустите приложение**
```bash
mvn spring-boot:run
```

5. **Откройте Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

## 📚 Документация

- [QUICKSTART.md](QUICKSTART.md) - Быстрый старт
- [IMPROVEMENTS.md](IMPROVEMENTS.md) - Детальное описание улучшений

## 🏗️ Архитектура

```
SupportPortal/
├── src/main/java/com/ostafon/supportportal/
│   ├── auth/              # Аутентификация
│   ├── users/             # Управление пользователями
│   ├── tickets/           # Система тикетов
│   ├── chat/              # Чат
│   ├── knowledgebase/     # База знаний
│   ├── notifications/     # Уведомления
│   ├── analytics/         # Аналитика
│   ├── admin/             # Админ панель
│   └── common/
│       ├── config/        # Конфигурация
│       ├── security/      # Безопасность
│       ├── exception/     # Обработка ошибок
│       ├── dto/           # Общие DTO
│       ├── utils/         # Утилиты
│       └── audit/         # Аудит
└── src/main/resources/
    ├── application.yml    # Конфигурация
    └── db/migration/      # Flyway миграции
```

```mermaid
graph TB
    subgraph "Client Layer"
        A[React Frontend<br/>SPA Application]
    end

    subgraph "API Gateway"
        B[Spring Boot Backend<br/>REST API<br/>Port: 8080]
    end

    subgraph "Security Layer"
        C[Spring Security<br/>JWT Authentication<br/>BCrypt Password]
    end

    subgraph "Business Logic"
        D[Auth Service]
        E[Ticket Service]
        F[User Service]
        G[Chat Service]
        H[Knowledge Base]
        I[Notification Service]
        J[Analytics Service]
        K[Admin Service]
    end

    subgraph "Data Layer"
        L[(PostgreSQL<br/>Database)]
        M[Flyway<br/>Migrations]
    end

    subgraph "External Services"
        N[SMTP Server<br/>Email Service]
        O[Swagger UI<br/>API Docs]
    end

    A -->|HTTP/HTTPS<br/>JSON| B
    B --> C
    C --> D
    C --> E
    C --> F
    C --> G
    C --> H
    C --> I
    C --> J
    C --> K

    D --> L
    E --> L
    F --> L
    G --> L
    H --> L
    I --> L
    J --> L
    K --> L

    M -.->|Database<br/>Migrations| L
    I -->|Send Email| N
    B -.->|API Documentation| O

    style A fill:#61dafb,stroke:#333,stroke-width:2px,color:#000
    style B fill:#6db33f,stroke:#333,stroke-width:2px,color:#fff
    style L fill:#336791,stroke:#333,stroke-width:2px,color:#fff
    style C fill:#f39c12,stroke:#333,stroke-width:2px,color:#000
```

```mermaid
flowchart LR
    subgraph Frontend["🖥️ Frontend Layer"]
        direction TB
        UI[React UI Components]
        Router[React Router]
        State[State Management<br/>Redux/Context]
        API[Axios/Fetch API]
    end

    subgraph Backend["⚙️ Backend Layer - Spring Boot 4.0.2"]
        direction TB
        Controller[REST Controllers]
        Security[Spring Security 7.x<br/>JWT Filter]

        subgraph Services["Business Services"]
            AuthSvc[Auth Service]
            TicketSvc[Ticket Service]
            UserSvc[User Service]
            ChatSvc[Chat Service]
            KBSvc[Knowledge Base]
            NotifSvc[Notification Service]
            AnalyticsSvc[Analytics Service]
            AdminSvc[Admin Service]
        end

        Repository[Spring Data JPA<br/>Repositories]
    end

    subgraph Data["💾 Data Layer"]
        DB[(PostgreSQL 14+<br/>HikariCP Pool)]
        Flyway[Flyway Migrations]
    end

    subgraph External["🌐 External Services"]
        SMTP[SMTP Email<br/>Port 587]
        Swagger[Swagger UI<br/>API Docs]
    end

    UI --> Router
    Router --> State
    State --> API
    API -->|JSON/JWT| Controller

    Controller --> Security
    Security --> Services

    Services --> Repository
    Repository -->|Hibernate ORM| DB
    Flyway -.->|Migrations| DB

    NotifSvc -->|Send Email| SMTP
    Controller -.->|Documentation| Swagger

    style Frontend fill:#61dafb20,stroke:#61dafb,stroke-width:3px
    style Backend fill:#6db33f20,stroke:#6db33f,stroke-width:3px
    style Data fill:#33679120,stroke:#336791,stroke-width:3px
    style External fill:#f39c1220,stroke:#f39c12,stroke-width:3px
```

```mermaid
sequenceDiagram
    participant U as 👤 User (React)
    participant F as Frontend (React)
    participant B as Backend (Spring Boot)
    participant S as Spring Security + JWT
    participant DB as PostgreSQL

    U->>F: 1. Ввод login/password
    F->>B: 2. POST /auth/login<br/>{email, password}
    B->>S: 3. Аутентификация
    S->>DB: 4. Проверка credentials
    DB-->>S: 5. User data
    S->>S: 6. Генерация JWT токена
    S-->>B: 7. JWT token
    B-->>F: 8. Response {token, user}
    F->>F: 9. Сохранить token в localStorage
    F-->>U: 10. Redirect to Dashboard

    Note over F,B: Последующие запросы
    U->>F: 11. Действие (например, GET /tickets)
    F->>B: 12. GET /tickets<br/>Header: Authorization: Bearer {JWT}
    B->>S: 13. Валидация JWT
    S-->>B: 14. User authenticated
    B->>DB: 15. Запрос данных
    DB-->>B: 16. Tickets data
    B-->>F: 17. Response {tickets[]}
    F-->>U: 18. Отображение данных
```

## 🔐 Безопасность

- ✅ JWT токены с HMAC-SHA256
- ✅ BCrypt хеширование паролей (strength 12)
- ✅ CORS правильно настроен
- ✅ CSRF защита (отключена для stateless API)
- ✅ Проверка активности пользователей
- ✅ Детальное логирование security событий

## 🔌 API Endpoints

### Аутентификация
```http
POST /auth/register - Регистрация
POST /auth/login    - Вход
```

### Тикеты
```http
GET    /tickets       - Список тикетов
POST   /tickets       - Создать тикет
GET    /tickets/{id}  - Детали тикета
PUT    /tickets/{id}  - Обновить тикет
DELETE /tickets/{id}  - Удалить тикет
```

Полная документация API доступна в Swagger UI.

## 🛠️ Технологии

- **Backend**: Spring Boot 4.0.2
- **Security**: Spring Security 7.x + JWT
- **Database**: PostgreSQL + Flyway
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Logging**: SLF4J + Logback

## 📊 Метрики проекта

- **Файлов Java**: 54
- **Строк кода**: 5000+
- **Покрытие тестами**: В разработке
- **Критических ошибок**: 0 ✅

## 🤝 Contributing

1. Fork проект
2. Создайте feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit изменения (`git commit -m 'Add AmazingFeature'`)
4. Push в branch (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

## 📝 License

Этот проект под лицензией MIT.

## 👤 Автор

**ostafon**


- Spring Framework Team
- PostgreSQL Community
- JWT.io

---

**Статус проекта**: ✅ Production Ready

**Версия**: 1.0.0

**Последнее обновление**: 2026-02-06

