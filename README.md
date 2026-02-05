# Dorothy

## Overview

Dorothy is a production-grade web service backend designed around  
**stateless authentication, scalable data access, and batch-based statistics aggregation**.

The project goes beyond basic CRUD functionality and focuses on:
- JWT-based authentication without server-side sessions
- Separation of operational data and analytical data
- Stable and performant statistics APIs through pre-aggregated tables
- A clean REST API structure for SPA clients

* This service is **currently running in a real production environment**.
* Statistics are treated as eventually consistent analytical data *
---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT (io.jsonwebtoken)
- Scheduled / Task-based batch processing

### Frontend
- React
- Vite

### Database
- MySql

---

## Architecture Overview
```
[ Frontend (React SPA) ]
|
| REST API
[ Spring Boot API Server ]
|
|-- Security (JWT, Stateless)
|-- Domain / Service Layer
|-- Statistics Batch Tasks
|
[ Database ]
├─ Operational Tables (Member, Reservation, etc.)
└─ Aggregated Statistics Tables (Daily Summaries)
```

## Authentication & Security

### Authentication Model
- Stateless authentication using JWT
- No HTTP session stored on the server

### Authentication Flow
1. User logs in and receives a JWT
2. JWT is stored in an HttpOnly, Secure cookie
3. Each request is intercepted by a custom authentication filter
4. Valid tokens populate the Spring SecurityContext

### Key Components
- `DorothyAuthFilter`
- `JwtTokenManager`
- `DorothyAuthToken`
- `WebSecurityConfig`

This design minimizes server-side state and allows horizontal scalability.

> The current implementation uses a single JWT.
> The structure is designed to allow future extension to an Access / Refresh Token model with rotation if needed.

---

## Statistics & Data Aggregation

### Design Motivation
Real-time aggregation on operational tables becomes expensive as data grows.
Statistics are treated as **eventually consistent analytical data**, not transactional data.

### Approach
- Separate operational data from statistical data
- Generate statistics through scheduled batch tasks
- Store results in dedicated aggregation tables

### StatisticsTask
- Runs on a scheduled basis
- Aggregates daily statistics from operational tables
- Writes results into summary tables

### Benefits
- Predictable performance for statistics APIs
- Reduced load on operational tables
- Clear separation between write-heavy and read-heavy workloads

---

## Package Structure
```
com.silverwing.dorothy
├─ api
│ └─ controller
├─ domain
│ ├─ entity
│ ├─ service
│ └─ security
├─ statistics
│ └─ task
└─ config
```

- **Controller**: HTTP request/response handling
- **Service**: Business logic
- **Security**: Authentication and authorization
- **Statistics**: Batch aggregation and analytical processing

---

## Data Access Strategy

- JPA is used for standard CRUD operations
- Statistics queries are separated from transactional queries
- Aggregated tables are treated as read-optimized data sources
- The design avoids expensive runtime aggregation in API requests

---

## Known Limitations & Future Improvements

- Automated test coverage for security and batch tasks can be expanded
- Statistics batch tasks can be enhanced with stronger idempotency guarantees
- Access / Refresh Token separation with rotation is a planned improvement
- Projection-based queries can further optimize statistics reads

---

## Running the Application

```
bash
# Backend
mvn clean install
java -jar target/dorothy-*.jar

# Frontend
npm install
npm run dev
```

## Deployment

The backend service is containerized using Docker and deployed as a Spring Boot executable JAR.

Sensitive configuration values (database credentials, JWT secrets, and third-party API keys) are injected at runtime via environment variables and are intentionally excluded from the public repository.