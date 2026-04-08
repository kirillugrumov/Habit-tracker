# HABIT TRACKER API

## REST API проект на Java, фреймворк Spring Boot, Maven, PostgreSQL.

Проект предоставляет набор готовых программных функций (API-запросов), с помощью которых можно управлять трекером привычек. Например, фронтенд-разработчик или мобильное приложение может использовать этот API, чтобы:
1. Создать пользователя
2. Создать и редактировать привычки
3. Отметить выполнение привычки
4. Посмотреть статистику выполнения
5. Управлять категориями привычек
6. Создавать цели для привычек

---

1ЛАБА:
1. Создать Spring Boot приложение.
2. Реализовать REST API для одной ключевой сущности своей предметной области (domain).
3. Реализовать:
   - GET endpoint с @RequestParam
   - GET endpoint с @PathVariable
4. Реализовать слои: Controller → Service → Repository.
5. Реализовать DTO и mapper между Entity и API-ответом.
6. Настроить Checkstyle и привести код к стилю.

---

2ЛАБА:
1. Подключить реляционную БД к проекту.
2. В модели данных реализовать минимум 5 сущностей:
   - минимум одну связь OneToMany
   - минимум одну связь ManyToMany
3. Реализовать CRUD операции.
4. Настроить и обосновать использование CascadeType и FetchType.
5. Продемонстрировать проблему N+1 и решить её через @EntityGraph или fetch join.
6. Реализовать метод, сохраняющий несколько связанных сущностей. Продемонстрировать частичное сохранение данных без @Transactional и полное откатывание операции с @Transactional при возникновении ошибки.
7. Нарисовать ER-диаграмму с указанием PK/FK и связей.

---

[Сонар](https://sonarcloud.io/projects)

## ER-диаграмма

```mermaid
erDiagram
    USERS ||--o{ HABITS : has
    HABITS ||--o{ HABIT_LOGS : tracks
    HABITS ||--o{ GOALS : has
    HABITS ||--o{ HABIT_CATEGORIES : belongs_to
    CATEGORIES ||--o{ HABIT_CATEGORIES : includes

    USERS {
        bigint id PK
        varchar email
        varchar username
    }

    HABITS {
        bigint id PK
        varchar name
        varchar description
        bigint user_id FK
    }

    HABIT_LOGS {
        bigint id PK
        date date
        bigint habit_id FK
    }

    GOALS {
        bigint id PK
        varchar name
        varchar condition
        bigint habit_id FK
    }

    CATEGORIES {
        bigint id PK
        varchar name
        varchar description
    }

    HABIT_CATEGORIES {
        bigint habit_id FK
        bigint category_id FK
    }
```
    
