# Database Design: Digital Knowledge Platform (PS029)

## 1. Database Philosophy: Database per Microservice
In alignment with microservice best practices, each business capability manages its own persistence store. This prevents database-level tight coupling and enables independent schema evolutions.

- **DBMS:** PostgreSQL
- **ORM / Data Access:** Spring Data JPA (Hibernate)
- **Host / Port:** `localhost:5432`

---

## 2. Databases & Schemas

### 2.1 Database: `dkp_auth`
#### Table: `users`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| `name` | VARCHAR(100) | NOT NULL | User's full name |
| `email` | VARCHAR(150) | UNIQUE, NOT NULL | User's login email address |
| `password` | VARCHAR(255) | NOT NULL | BCrypt salted hash of password |
| `role` | VARCHAR(50) | NOT NULL | e.g. `STUDENT`, `INSTRUCTOR`, `ADMIN` |

---

### 2.2 Database: `dkp_content`
#### Table: `contents`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGSERIAL | PRIMARY KEY | Unique content identifier |
| `title` | VARCHAR(200) | NOT NULL | Resource title |
| `author` | VARCHAR(100) | NOT NULL | Creator / Author name |
| `content_type` | VARCHAR(50) | NOT NULL | e.g. `BOOK`, `ARTICLE`, `VIDEO` |
| `category` | VARCHAR(100) | NOT NULL | Subject category (e.g. `PROGRAMMING`) |
| `description` | TEXT | NULLABLE | Detailed description |
| `resource_url` | VARCHAR(500) | NOT NULL | Link / file path to resource |

---

### 2.3 Database: `dkp_access`
#### Table: `access_permissions`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGSERIAL | PRIMARY KEY | Unique permission grant ID |
| `user_id` | BIGINT | NOT NULL | Target user ID |
| `content_id` | BIGINT | NOT NULL | Target content ID |
| `access_type` | VARCHAR(50) | NOT NULL | Permission type (`READ`, `DOWNLOAD`) |
| `start_date` | TIMESTAMP | NOT NULL | Grant start timestamp |
| `end_date` | TIMESTAMP | NULLABLE | Expiry timestamp |
| `status` | VARCHAR(20) | NOT NULL | `ACTIVE`, `EXPIRED`, `REVOKED` |

---

### 2.4 Database: `dkp_usage`
#### Table: `reading_history`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGSERIAL | PRIMARY KEY | Activity log identifier |
| `user_id` | BIGINT | NOT NULL | Student / User ID |
| `content_id` | BIGINT | NOT NULL | Content item ID |
| `action` | VARCHAR(50) | NOT NULL | Action performed (`READ`, `DOWNLOAD`) |
| `started_at` | TIMESTAMP | NOT NULL | Reading session start time |
| `last_accessed`| TIMESTAMP | NOT NULL | Last accessed timestamp |
| `duration` | INT | NOT NULL | Duration in minutes |
