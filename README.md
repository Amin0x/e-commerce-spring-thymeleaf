# E-commerce (Thymeleaf frontend)

Thymeleaf server-rendered UI for the e-commerce application. It calls a separately
deployed REST backend over HTTP.

- **This repository** — Thymeleaf UI, session-based auth, REST client layer (port 8080).
- **Backend API** — maintained in a separate repository:
  <https://github.com/Amin0x/e-commerce-backend> (port 8081).

## Requirements

- JDK 17 or 21 (Maven `java.version` property controls the release target)
- A running backend API (see the repository linked above)

## Configuration

| Property | Default | Notes |
|----------|---------|-------|
| `server.port` | `8080` | |
| `backend.base-url` | `http://localhost:8081` | Backend API base URL |

See `src/main/resources/application.properties`.

## Authentication flow

1. Browser posts to `/auth/login`.
2. The frontend calls backend `POST /api/auth/login`, receives a JWT.
3. The JWT + role are stored in the servlet session (browser never sees the JWT).
4. `SessionAuthenticationFilter` rebuilds the Spring Security context from the session on each request.
5. `ClientAuthInterceptor` attaches `Authorization: Bearer <token>` to every backend call.
6. The backend enforces all authorization rules independently.

## Building and running

```bash
# Build
./mvnw package -DskipTests

# Run
java -jar target/frontend.jar
```

The frontend is reachable at `http://localhost:8080`; the backend API is expected at
`http://localhost:8081`.
