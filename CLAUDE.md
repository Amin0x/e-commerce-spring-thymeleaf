# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Use `gh-axi` for GitHub and `chrome-devtools-axi` for browser automation.

## Project overview

Thymeleaf server-rendered frontend for an e-commerce application, built with Spring Boot 4.1.2.
This repository is the frontend project root. The REST backend lives in a **separate repository**:
<https://github.com/Amin0x/e-commerce-backend> (port 8081).

| Component | Package | Description | Port |
|-----------|---------|-------------|------|
| Frontend | `com.alamin.ecommerce.frontend` | Thymeleaf UI that calls the backend REST API via `RestTemplate` | 8080 |

Commands run from the repo root:

```bash
# Build (skip tests)
./mvnw package -DskipTests

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=FrontendContextTest

# Run a single test method
./mvnw test -Dtest=FrontendContextTest#testMethodName

# Run the frontend
java -jar target/frontend.jar
```

The frontend is reachable at `http://localhost:8080`; the backend API is expected at `http://localhost:8081`.

Java version: the POM targets Java 21 by default; JDK 17 or 21 are supported via the `java.version`
Maven property (e.g. `./mvnw package -Djava.version=17`).

## Build configuration notes

- The build uses `frontend-maven-plugin` to install Node/npm locally (under `node/`) and run `npm run css:build` (PostCSS/Tailwind) during `generate-resources`, regenerating `src/main/resources/static/public/css/style.css` on every build. Node/npm do not need to be pre-installed.
- Maven wrapper: `./mvnw` (and `./mvnw.cmd` on Windows).
- No lint/format plugin is configured; match the existing code style (Lombok `@RequiredArgsConstructor`, `@RestController`/`@Controller`, constructor injection).

## Architecture — key boundaries

**Frontend → backend communication** (`src/main/java/com/alamin/ecommerce/frontend/client/`):

- `ClientConfig` builds a single `RestTemplate`, wired with `ClientAuthInterceptor`.
- `ClientAuthInterceptor` attaches `Authorization: Bearer <jwt>` (from the servlet session) and `X-Cart-Key` (stable per-session cart identity) to every outbound backend call.
- API clients live under `client/api/` (interfaces + `*Impl` classes backed by `RestTemplate`): `AuthClient`, `ProductClient`, `CategoryClient`, `CartClient`, `AdminProductClient`, `AdminCategoryClient`, `AdminUserClient`, `AdminOrderClient`, `AdminStatisticsClient`, `SettingClient`, `SubscribeClient`, `AddressClient`. DTOs under `dto/` mirror backend request/response shapes.
- On successful login/register, `AuthClientImpl` stores the JWT + role in the HTTP session; the browser never sees the JWT.

**Authentication split:**

- **Backend** (separate repository): stateless JWT, `SessionCreationPolicy.STATELESS`, CSRF disabled, CORS restricted to the frontend origin.
- **Frontend** (`src/main/java/com/alamin/ecommerce/frontend/security/SecurityConfig.java`): session-backed. `SessionAuthenticationFilter` rebuilds the Spring Security context from the servlet session on each request (JWT + role stored by `AuthClientImpl`). Grants `ROLE_ADMIN` when the stored role is `ADMIN`, else `ROLE_ADMIN`-guarded `/admin/**` routes. CSRF disabled, stateless session management.

**Data flow:**

1. Browser posts to `/auth/login` → frontend `AuthController` → `AuthClient.login()` calls backend `POST /api/auth/login`, receives JWT.
2. JWT + role stored in servlet session; `SessionAuthenticationFilter` populates Spring Security context on each request.
3. `ClientAuthInterceptor` attaches the JWT to all backend REST calls.
4. Backend enforces all authorization independently.

**Configuration:**

- `src/main/resources/application.properties` — port 8080, `backend.base-url=http://localhost:8081`.

## Tests

- Tests live in `src/test/java/com/alamin/ecommerce/frontend/` (context test, client contract deserialization test).
- Backend tests live in the separate backend repository.
- No dedicated test runner beyond Maven Surefire; use `-Dtest=<ClassName>[#method]` to scope.

## What to avoid

- Do not list every file or component — the structure above is discoverable from the source tree.
- Do not add generic development advice; this file covers only repo-specific commands, architecture, and boundaries.
