# E-commerce (Spring Boot + Thymeleaf)

Split into two independently deployable Maven subprojects:

- **`backend/`** — REST API only, secured with Spring Security + JWT (port 8081).
- **`frontend/`** — Thymeleaf server-rendered UI that calls the backend API (port 8080).

Both modules share the repository root (git at root). Build everything from the root:

```bash
mvn package -Djava.version=17
```

## Modules

| Module | Description | Port |
|--------|-------------|------|
| `backend` | JSON REST API, Spring Security JWT, MySQL, mail | 8081 |
| `frontend` | Thymeleaf UI, session-based auth, REST client layer | 8080 |

## Requirements

- JDK 17 or 21 (Maven `java.version` property controls the release target)
- MySQL database (backend only)
- SMTP server for mail (backend only)

## Backend configuration

| Property | Default | Notes |
|----------|---------|-------|
| `server.port` | `8081` | |
| `app.jwt.secret` | required | ≥256 bits; overridable via `JWT_SECRET` env var |
| `app.jwt.access-token-expiration` | `86400000` (24h) | ms |
| `app.upload.dir` | `C:/uploads/` | |
| `app.cors.allowed-origins` | `http://localhost:8080` | |
| `spring.datasource.*` | — | MySQL connection |

## Frontend configuration

| Property | Default | Notes |
|----------|---------|-------|
| `server.port` | `8080` | |
| `backend.base-url` | `http://localhost:8081` | Backend API base URL |

## Authentication flow

1. Browser posts to frontend `/auth/login`.
2. Frontend calls backend `POST /api/auth/login`, receives a JWT.
3. Frontend stores the JWT + role in the servlet session (browser never sees the JWT).
4. `SessionAuthenticationFilter` rebuilds the Spring Security context from the session on each request.
5. `ClientAuthInterceptor` attaches `Authorization: Bearer <token>` to every backend call.
6. Backend enforces all authorization rules independently.

## Building and running

```bash
# Build both modules
mvn package -Djava.version=17 -DskipTests

# Run backend
java -jar backend/target/backend.jar

# Run frontend (in another terminal)
java -jar frontend/target/frontend.jar
```

The frontend is reachable at `http://localhost:8080`; the backend API at `http://localhost:8081`.
