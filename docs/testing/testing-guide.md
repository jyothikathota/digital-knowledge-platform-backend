# Testing Guide: Digital Knowledge Platform (PS029)

## 1. Overview
This testing guide provides step-by-step procedures for testing each microservice using:
- Automated Maven Unit/Integration Tests
- Manual REST Client / Postman / cURL requests

---

## 2. Automated Testing

To run unit tests across all services from root:
```powershell
.\mvnw.cmd test
```

To run unit tests for an individual service:
```powershell
.\mvnw.cmd test -pl discovery-server
```

---

## 3. Postman / REST Client API Testing Guide

### 3.1 Eureka Discovery Server (Port 9000)
- **URL:** `http://localhost:9000`
- **Method:** `GET`
- **Expected Result:** Eureka dashboard HTML showing "Instances currently registered with Eureka".

---

### 3.2 Auth Service Testing (Port 8081 / Gateway Port 8000)
#### Register User
- **URL:** `http://localhost:8000/api/auth/register` (or `http://localhost:8081/api/auth/register`)
- **Method:** `POST`
- **Header:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "name": "Jyothika",
    "email": "jyothika@example.com",
    "password": "Password@123",
    "role": "STUDENT"
  }
  ```
- **Expected Response:** HTTP 201 Created with created user details (excluding password).

#### User Login
- **URL:** `http://localhost:8000/api/auth/login` (or `http://localhost:8081/api/auth/login`)
- **Method:** `POST`
- **Header:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "email": "jyothika@example.com",
    "password": "Password@123"
  }
  ```
- **Expected Response:** HTTP 200 OK with `token` (JWT).

---

### 3.3 Content Service Testing (Port 8082 / Gateway Port 8000)
#### Create Content
- **URL:** `http://localhost:8000/api/content`
- **Method:** `POST`
- **Header:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "title": "Java Programming",
    "author": "Deepak",
    "contentType": "BOOK",
    "category": "PROGRAMMING",
    "description": "Java programming learning resource",
    "resourceUrl": "https://example.com/java"
  }
  ```
- **Expected Response:** HTTP 201 Created with generated `id`.

#### Search Content
- **URL:** `http://localhost:8000/api/content/search?keyword=java`
- **Method:** `GET`
- **Expected Response:** HTTP 200 OK with matching items.

---

### 3.4 Access Service Testing (Port 8083 / Gateway Port 8000)
#### Grant Access
- **URL:** `http://localhost:8000/api/access/grant`
- **Method:** `POST`
- **Header:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "userId": 1,
    "contentId": 1,
    "accessType": "READ",
    "endDate": "2026-12-31T23:59:59"
  }
  ```
- **Expected Response:** HTTP 201 Created with permission status `ACTIVE`.

#### Check Access
- **URL:** `http://localhost:8000/api/access/check/1/1`
- **Method:** `GET`
- **Expected Response:** HTTP 200 OK with permission validation and content info.

---

### 3.5 Usage Service Testing (Port 8084 / Gateway Port 8000)
#### Record Reading Activity
- **URL:** `http://localhost:8000/api/usage/record`
- **Method:** `POST`
- **Header:** `Content-Type: application/json`
- **Body:**
  ```json
  {
    "userId": 1,
    "contentId": 1,
    "action": "READ",
    "duration": 30
  }
  ```
- **Expected Response:** HTTP 201 Created with timestamp logged.

#### Retrieve History
- **URL:** `http://localhost:8000/api/usage/history/1`
- **Method:** `GET`
- **Expected Response:** HTTP 200 OK with list of past reading sessions.
