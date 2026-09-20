# Digital Knowledge Platform & Content Access Management System
**Project ID:** PS029  
**Academic Review:** Review-1  
**Architecture:** Service-Oriented Architecture (SOA) / Microservices  

---

## 1. Problem Statement
Educational institutions and modern digital repositories handle thousands of learning resources across varied departments and academic levels. Traditional monolithic platforms suffer from single points of failure, tight coupling between authentication, content delivery, and usage tracking, and inflexible permission policies. Students and faculty require secure, role-governed, fast, and scalable access to books, papers, and media with precise activity tracking.

## 2. Objectives
1. **Modular Separation**: Decouple Identity, Resource Catalog, Permission Control, and Telemetry into discrete, independently deployable microservices.
2. **Centralized Discovery & Routing**: Employ Netflix Eureka for dynamic service registration and Spring Cloud Gateway for unified client entry, reverse proxying, and route aggregation.
3. **Stateless Security**: Enforce token-based authentication using JSON Web Tokens (JWT) and BCrypt salted password hashing.
4. **Reliable Inter-Service Communication**: Facilitate synchronous REST-based communication between Access Service and Content Service to validate content availability before granting or checking permissions.
5. **Auditing & Telemetry**: Maintain detailed reading activity, sessions, and duration in Usage Service.

---

## 3. Technology Stack
- **Programming Language:** Java 25 (with LTS compatibility)
- **Framework:** Spring Boot 3.4.3
- **Microservices Orchestration:** Spring Cloud 2024.0.0 (Eureka Discovery Server, Spring Cloud Gateway)
- **Security & Authorization:** Spring Security, JSON Web Token (JJWT 0.12.6), BCrypt
- **Database:** PostgreSQL (Spring Data JPA / Hibernate)
- **Inter-Service Communication:** Spring REST Client
- **Build & Dependency Tool:** Apache Maven (Maven Wrapper `mvnw`)
- **API Testing:** Postman / REST Client / cURL

---

## 4. Microservices & Port Allocation

| # | Service Name | Port | Description | Database |
|---|--------------|------|-------------|----------|
| 1 | **Eureka Server** (`discovery-server`) | `9000` | Dynamic service registration and discovery registry | None (In-memory registry) |
| 2 | **API Gateway** (`api-gateway`) | `8000` | Single entry point, routing requests to services | None |
| 3 | **Auth Service** (`auth-service`) | `8081` | User registration, login, BCrypt hashing, JWT generation | `dkp_auth` (`users` table) |
| 4 | **Content Service** (`content-service`) | `8082` | Digital resource management, search, cataloging | `dkp_content` (`contents` table) |
| 5 | **Access Service** (`access-service`) | `8083` | Permission grants, revocation, access authorization | `dkp_access` (`access_permissions` table) |
| 6 | **Usage Service** (`usage-service`) | `8084` | Reading activity, download tracking, history logging | `dkp_usage` (`reading_history` table) |

---

## 5. System Architecture & Project Flow

```
                                +-------------------+
                                |    Client/Browser |
                                |   (Postman/Web)   |
                                +---------+---------+
                                          |
                                    HTTP Requests
                                          |
                                          v
                                +-------------------+
                                |    API Gateway    |  Port: 8000
                                |  (Spring Cloud)   |
                                +----+----+----+----+
                                     |    |    |
        +----------------------------+    |    +---------------------------+
        |                                 |                                |
        v                                 v                                v
+---------------+                 +---------------+                +---------------+
| Auth Service  | Port: 8081      |Content Service| Port: 8082     | Usage Service | Port: 8084
| (Users & JWT) |                 |  (Resources)  |                | (Read History)|
+---------------+                 +-------^-------+                +---------------+
                                          |
                                    REST Client
                                  (Verify Content)
                                          |
                                  +-------+-------+
                                  | Access Service| Port: 8083
                                  | (Permissions) |
                                  +---------------+

               All microservices dynamically register with:
                        +----------------------+
                        |   Discovery Server   | Port: 9000
                        |   (Netflix Eureka)   |
                        +----------------------+
```

### End-to-End Execution Flow:
1. **User Registration & Login**: Client calls `/api/auth/register` and `/api/auth/login`. Auth Service validates credentials using BCrypt and issues a signed JWT.
2. **Resource Ingestion**: Content Managers or Admins post learning material to `/api/content`.
3. **Access Authorization**: When a student requests a resource, Access Service checks permissions in `access_permissions` and verifies content existence via Content Service over REST.
4. **Telemetry & Reading History**: Once access is granted and reading starts, Usage Service records reading sessions, action types, and duration via `/api/usage/record`.

---

## 6. Database Schema Overview

### 1. `users` (`dkp_auth`)
- `id` (BIGSERIAL PRIMARY KEY)
- `name` (VARCHAR(100) NOT NULL)
- `email` (VARCHAR(150) UNIQUE NOT NULL)
- `password` (VARCHAR(255) NOT NULL - BCrypt hashed)
- `role` (VARCHAR(50) NOT NULL - `STUDENT`, `INSTRUCTOR`, `ADMIN`)

### 2. `contents` (`dkp_content`)
- `id` (BIGSERIAL PRIMARY KEY)
- `title` (VARCHAR(200) NOT NULL)
- `author` (VARCHAR(100) NOT NULL)
- `content_type` (VARCHAR(50) NOT NULL - e.g., `BOOK`, `ARTICLE`, `VIDEO`)
- `category` (VARCHAR(100) NOT NULL)
- `description` (TEXT)
- `resource_url` (VARCHAR(500) NOT NULL)

### 3. `access_permissions` (`dkp_access`)
- `id` (BIGSERIAL PRIMARY KEY)
- `user_id` (BIGINT NOT NULL)
- `content_id` (BIGINT NOT NULL)
- `access_type` (VARCHAR(50) NOT NULL - e.g., `READ`, `DOWNLOAD`)
- `start_date` (TIMESTAMP NOT NULL)
- `end_date` (TIMESTAMP)
- `status` (VARCHAR(20) NOT NULL - `ACTIVE`, `EXPIRED`, `REVOKED`)

### 4. `reading_history` (`dkp_usage`)
- `id` (BIGSERIAL PRIMARY KEY)
- `user_id` (BIGINT NOT NULL)
- `content_id` (BIGINT NOT NULL)
- `action` (VARCHAR(50) NOT NULL - `READ`, `DOWNLOAD`)
- `started_at` (TIMESTAMP NOT NULL)
- `last_accessed` (TIMESTAMP NOT NULL)
- `duration` (INT NOT NULL - in minutes)

---

## 7. API Reference

### Auth Service (`/api/auth`)
- `POST /api/auth/register` : Register a new user
- `POST /api/auth/login` : Authenticate user & return JWT token

### Content Service (`/api/content`)
- `POST /api/content` : Create new content
- `GET /api/content` : Retrieve all contents
- `GET /api/content/{id}` : Retrieve content by ID
- `PUT /api/content/{id}` : Update content details
- `DELETE /api/content/{id}` : Delete content
- `GET /api/content/search?keyword={keyword}` : Search by title or author

### Access Service (`/api/access`)
- `POST /api/access/grant` : Grant content access permission
- `GET /api/access/check/{userId}/{contentId}` : Verify access status
- `DELETE /api/access/{id}` : Revoke permission

### Usage Service (`/api/usage`)
- `POST /api/usage/record` : Log user reading activity
- `GET /api/usage/history/{userId}` : Retrieve reading history for user

---

## 8. Step-by-Step Execution Guide

### Prerequisites
- Java 21 or Java 25 installed (`java -version`)
- PostgreSQL installed and running on `localhost:5432`

### Running the Services
Start the services in the following order:

```powershell
# 1. Start Eureka Discovery Server (Port 9000)
.\mvnw.cmd spring-boot:run -pl discovery-server

# 2. Start Auth Service (Port 8081)
.\mvnw.cmd spring-boot:run -pl auth-service

# 3. Start Content Service (Port 8082)
.\mvnw.cmd spring-boot:run -pl content-service

# 4. Start Access Service (Port 8083)
.\mvnw.cmd spring-boot:run -pl access-service

# 5. Start Usage Service (Port 8084)
.\mvnw.cmd spring-boot:run -pl usage-service

# 6. Start API Gateway (Port 8000)
.\mvnw.cmd spring-boot:run -pl api-gateway
```

### Eureka Dashboard
Open `http://localhost:9000` in any web browser to view registered microservice instances.
