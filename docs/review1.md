# College Review-1 Documentation
**Project Title:** Digital Knowledge Platform & Content Access Management System  
**Project ID:** PS029  
**Review Stage:** Review-1 (Problem Identification, Architecture, & Design)  

---

## 1. Problem Analysis
Academic organizations and higher educational institutions produce and distribute an overwhelming amount of learning materials (books, research publications, lecture slides, coding walkthroughs). Existing institutional platforms encounter several bottlenecks:
- **Monolithic Inefficiencies**: Intertwining cataloging, authentication, and usage reporting slows down system modifications and scalability.
- **Access Management Vulnerabilities**: Coarse-grained authorization fails to grant time-bound or role-specific privileges.
- **Absence of Centralized Auditing**: Institutions struggle to track which resources students engage with, preventing data-driven curriculum enhancements.

## 2. Requirement Specification

### Functional Requirements
1. **User Authentication & Authorization**: Registration, login, role management (`STUDENT`, `INSTRUCTOR`, `ADMIN`), and stateless JWT token issuance.
2. **Digital Resource Cataloging**: Full CRUD operations on educational contents, category filtering, and keyword search.
3. **Access Control**: Role-based and time-bound permission checks to govern who can read or download content.
4. **Usage Analytics**: Real-time recording of reading sessions, duration, and user interaction history.
5. **Dynamic Routing & Registry**: Centralized routing through an API Gateway with automated service discovery via Eureka.

### Non-Functional Requirements
1. **Scalability**: Decoupled microservices capable of independent horizontal scaling.
2. **Security**: Passwords encrypted using BCrypt (cost factor 10+); zero plain-text passwords in database and API responses.
3. **Fault Tolerance**: Non-cascading errors across independent microservices.
4. **Maintainability**: Clean package structure adhering to Controller-Service-Repository patterns.

---

## 3. Survey Findings
*(Note: As per project guidelines, real survey responses from the team will be entered below. Do not invent fake data.)*

- **Target Audience Sample Size:** `[Insert Team Survey Sample Size, e.g., 50 Students & 15 Faculty Members]`
- **Key Survey Questions Asked:**
  1. How frequently do you encounter broken or slow resource links on the current library platform?
  2. Would you prefer automated access approval based on your enrolled courses?
  3. How important is personalized reading history and tracking for your exam revision?
- **Data Summary / Findings:**
  - `[Placeholder: Enter percentage or count of respondents who cited access delays]`
  - `[Placeholder: Enter percentage of respondents requesting unified keyword search across subjects]`
  - `[Placeholder: Enter summary of faculty feedback regarding unauthorized resource sharing]`

---

## 4. Common Issues Identified in Existing Systems
1. **Single Point of Failure**: If the monolith server crashes, library access, authentication, and reporting go down simultaneously.
2. **Session Bottlenecks**: Server-side sticky sessions overload web servers during exam periods.
3. **Inconsistent Permission Enforcement**: Different departments use isolated portals with inconsistent authorization policies.
4. **Lack of Telemetry**: Inability to quantify resource adoption or measure student reading engagement.

---

## 5. Empathy Map

| Category | Description |
|---|---|
| **Says** | "I want quick access to my recommended textbooks without repeated logins." / "I need to know how many hours students spent on course materials." |
| **Thinks** | "Will I lose access right before the submission deadline?" / "Is my academic data secure?" |
| **Does** | Searches across multiple repositories, downloads materials locally, submits access requests manually. |
| **Feels** | Frustrated by slow searches and confusing permission denials; overwhelmed by fragmented resource sites. |

---

## 6. User Persona

### Persona 1: The Undergraduate Student
- **Name:** Jyothika S.
- **Role:** 3rd Year Computer Science Student
- **Goals:** Quickly locate reference books for ongoing semester courses; view reading progress and study materials on-demand.
- **Frustrations:** Encountering permission denied errors without clear instructions; slow search filters when browsing textbooks.

### Persona 2: The Faculty Instructor
- **Name:** Dr. R. Sharma
- **Role:** Head of Department / Course Coordinator
- **Goals:** Upload course lecture notes, grant temporary access to specific cohorts, and monitor reading analytics.
- **Frustrations:** Lack of visibility into whether enrolled students have reviewed mandatory reference articles.

---

## 7. Customer Journey Map

| Stage | Actions | Thoughts | Pain Points | System Touchpoint |
|---|---|---|---|---|
| **1. Discovery** | Visits student portal, registers for account | "Registration should be fast and simple." | Complex verification steps | `POST /api/auth/register` |
| **2. Authentication** | Logs in using credentials | "I hope I stay logged in during my study session." | Session timeouts | `POST /api/auth/login` (JWT) |
| **3. Search Catalog** | Enters query "Java Programming" | "I need the latest syllabus book immediately." | Irrelevant search results | `GET /api/content/search?keyword=...` |
| **4. Request Access** | Requests read permission for book | "Will I be approved right away?" | Delayed manual approval | `POST /api/access/grant` |
| **5. Reading & Tracking**| Reads content; session tracked | "I want to resume where I left off." | Lost reading progress | `POST /api/usage/record` |

---

## 8. Architecture Diagram

```
                              [ Client Applications ]
                       (Web Browser / Postman / Mobile App)
                                        |
                                        v
                          +----------------------------+
                          |   Spring Cloud Gateway     | : Port 8000
                          |  (/api/auth/**, /api/**)   |
                          +--------------+-------------+
                                         |
            +----------------------------+---------------------------+
            |                            |                           |
            v                            v                           v
  +-------------------+        +-------------------+       +-------------------+
  |   Auth Service    |        |  Content Service  |       |   Usage Service   |
  |    : Port 8081    |        |    : Port 8082    |       |    : Port 8084    |
  |  (Users, JWT)     |        | (Digital Catalog) |       |  (Read Telemetry) |
  +---------+---------+        +---------+---------+       +---------+---------+
            |                            ^                           |
            v                            | REST Client               v
      [( dkp_auth )]                     |                     [( dkp_usage )]
                               +---------+---------+
                               |  Access Service   |
                               |    : Port 8083    |
                               | (Permissions)     |
                               +---------+---------+
                                         |
                                         v
                                   [( dkp_access )]

                 +-----------------------------------------------+
                 |        Eureka Discovery Server : Port 9000    |
                 +-----------------------------------------------+
```

---

## 9. Modules & Microservices Listing

1. **`discovery-server` (Port 9000)**: Netflix Eureka Server registry providing service lookup and heartbeat monitoring.
2. **`api-gateway` (Port 8000)**: Spring Cloud Gateway acting as the reverse proxy, forwarding API paths (`/api/auth/**`, `/api/content/**`, `/api/access/**`, `/api/usage/**`).
3. **`auth-service` (Port 8081)**: Manages credentials, BCrypt password hashing, and signed JWT issuance.
4. **`content-service` (Port 8082)**: Handles digital learning resources, metadata, categories, and keyword searches.
5. **`access-service` (Port 8083)**: Governs permission grants/revocations; verifies content with Content Service over REST.
6. **`usage-service` (Port 8084)**: Tracks reading and download telemetry, session durations, and student reading histories.

---

## 10. Data Handling & Database Schemas
- Individual databases: `dkp_auth`, `dkp_content`, `dkp_access`, `dkp_usage` (PostgreSQL).
- Entities mapped using Spring Data JPA (`@Entity`, `@Table`, `@Id`).
- Sensitive data: Passwords stored strictly as BCrypt hashes; never returned in DTOs.
- Timestamps: ISO-8601 compliant `java.time.LocalDateTime`.

---

## 11. Service Integration
- **Inter-Service REST Protocol**: Access Service queries Content Service via Spring's `RestClient` (`GET http://localhost:8082/api/content/{id}`).
- **Discovery Registration**: Each service auto-registers with Eureka under its `spring.application.name`.
- **Gateway Dynamic Routing**: Gateway dynamically resolves target service locations via Eureka registry.

---

## 12. Innovative Ideas
1. **Granular Time-Bound Access**: Grant permissions that auto-expire on a semester completion date.
2. **Automated Reading Insights**: Generate study duration analytics to help students pace their exam preparations.
3. **Stateless Scalability**: Zero session state stored in gateway or backend services, allowing effortless scaling.
