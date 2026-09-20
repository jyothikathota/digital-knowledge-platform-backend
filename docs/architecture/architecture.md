# Architecture Documentation: Digital Knowledge Platform (PS029)

## 1. Overview
The Digital Knowledge Platform & Content Access Management System is structured around the Microservices Architecture Pattern. Instead of a single monolithic deployment, responsibilities are partitioned into loosely-coupled, highly cohesive autonomous services.

## 2. Component Responsibilities

### 2.1 Discovery Server (Netflix Eureka)
- **Port:** 9000
- **Package:** `com.dkp.discovery`
- **Role:** Acts as the phonebook for microservices.
- **Key Behavior:**
  - Microservices register their network location (IP/host and port) upon boot.
  - Periodic heartbeats keep registrations alive.
  - The discovery server itself is configured with `register-with-eureka=false` so it does not pollute its own registry.

### 2.2 API Gateway (Spring Cloud Gateway)
- **Port:** 8000
- **Package:** `com.dkp.gateway`
- **Role:** Reverse proxy and single entry point for all external client traffic.
- **Key Routes:**
  - `/api/auth/**` $\rightarrow$ `auth-service` (Port 8081)
  - `/api/content/**` $\rightarrow$ `content-service` (Port 8082)
  - `/api/access/**` $\rightarrow$ `access-service` (Port 8083)
  - `/api/usage/**` $\rightarrow$ `usage-service` (Port 8084)

### 2.3 Auth Service
- **Port:** 8081
- **Package:** `com.dkp.auth`
- **Role:** Handles user lifecycle, credentials, BCrypt password hashing, and token generation.

### 2.4 Content Service
- **Port:** 8082
- **Package:** `com.dkp.content`
- **Role:** Digital catalog manager. Handles resource creation, retrieval, updates, deletions, and keyword search.

### 2.5 Access Service
- **Port:** 8083
- **Package:** `com.dkp.access`
- **Role:** Permission engine. Before validating or granting permissions, Access Service communicates synchronously via REST with Content Service to confirm the content exists.

### 2.6 Usage Service
- **Port:** 8084
- **Package:** `com.dkp.usage`
- **Role:** Telemetry & Analytics engine. Records reading activity, sessions, durations, and past engagement.

## 3. Communication Patterns
- **External to Gateway:** HTTP/JSON REST APIs.
- **Gateway to Services:** Routed HTTP/JSON REST APIs.
- **Inter-Service Communication:** Synchronous REST calls using Spring's modern `RestClient`.
