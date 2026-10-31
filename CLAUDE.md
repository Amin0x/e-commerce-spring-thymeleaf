# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Split e-commerce application built with Spring Boot 4.1.2, Thymeleaf, and MySQL. Two independently deployable Maven subprojects share this repository (git at root):

| Module | Package | Description | Port |
|--------|---------|-------------|------|
| `backend` | `com.alamin.ecommerce` | JSON REST API, Spring Security + JWT, Spring Data JPA + MySQL, Spring Mail, iText PDF | 8081 |
| `frontend` | `com.alamin.ecommerce.frontend` | Thymeleaf server-rendered UI that calls the backend REST API via `RestTemplate` | 8080 |

Build everything from the repo root:

```bash
# Build both modules (skip tests)
./mvnw package -DskipTests

# Run tests for both modules
./mvnw test

# Run a single test class
./mvnw test -Dtest=CategoryServiceImplTest

# Run a single test method
./mvnw test -Dtest=CategoryServiceImplTest#testMethodName

# Run tests in one module only
./mvnw test -pl backend
./mvnw test -pl frontend

# Run backend
java -jar backend/target/backend.jar

# Run frontend (separate terminal)
java -jar frontend/target/frontend.jar
```

The frontend is reachable at `http://localhost:8080`; the backend API at `http://localhost:8081`.

Java version: the root POM targets Java 21; the backend module overrides to Java 26. The README notes JDK 17 or 21 are supported via the `java.version` Maven property (e.g. `./mvnw package -Djava.version=17`).

## Build configuration notes

- The **frontend** module uses `frontend-maven-plugin` to install Node/npm locally and run `npm run css:build` (PostCSS/Tailwind) during `generate-resources`, regenerating `src/main/resources/static/public/css/style.css` on every build. Node/npm do not need to be pre-installed.
- Maven wrapper: `./mvnw` (and `./mvnw.cmd` on Windows).
- No lint/format plugin is configured; match the existing code style (Lombok `@RequiredArgsConstructor`, `@RestController`/`@Controller`, constructor injection).

## Architecture — key cross-module boundaries

**Frontend → backend communication** (`frontend/src/main/java/com/alamin/ecommerce/frontend/client/`):

- `ClientConfig` builds a single `RestTemplate` per frontend module, wired with `ClientAuthInterceptor`.
- `ClientAuthInterceptor` attaches `Authorization: Bearer <jwt>` (from the servlet session) and `X-Cart-Key` (stable per-session cart identity) to every outbound backend call.
- API clients live under `client/api/` (interfaces + `*Impl` classes backed by `RestTemplate`): `AuthClient`, `ProductClient`, `CategoryClient`, `CartClient`, `AdminProductClient`, `AdminCategoryClient`, `AdminUserClient`, `AdminOrderClient`, `AdminStatisticsClient`, `SettingClient`, `SubscribeClient`, `AddressClient`. DTOs under `client/api/../dto/` mirror backend request/response shapes.
- On successful login/register, `AuthClientImpl` stores the JWT + role in the HTTP session; the browser never sees the JWT.

**Authentication split:**

- **Backend** (`backend/.../config/SecurityConfig.java`): stateless JWT. `SessionCreationPolicy.STATELESS`, CSRF disabled, `JwtAuthenticationFilter` validates Bearer tokens ahead of the username/password filter, 401/403 returned as JSON via `RestAuthenticationEntryPoint` / `RestAccessDeniedHandler`. CORS restricted to the frontend origin (`app.cors.allowed-origins`). `@EnableMethodSecurity(prePostEnabled = true)`.
- **Frontend** (`frontend/.../security/SecurityConfig.java`): session-backed. `SessionAuthenticationFilter` rebuilds the Spring Security context from the servlet session on each request (JWT + role stored by `AuthClientImpl`). Grants `ROLE_ADMIN` when the stored role is `ADMIN`, else `ROLE_ADMIN`-guarded `/admin/**` routes. CSRF disabled, stateless session management.

**Data flow:**

1. Browser posts to frontend `/auth/login` → frontend `AuthController` → `AuthClient.login()` calls backend `POST /api/auth/login`, receives JWT.
2. JWT + role stored in servlet session; `SessionAuthenticationFilter` populates Spring Security context on each request.
3. `ClientAuthInterceptor` attaches the JWT to all backend REST calls.
4. Backend enforces all authorization independently.

**Configuration:**

- `backend/src/main/resources/application.properties` — port 8081, MySQL datasource (`jdbc:mysql://localhost:3306/amin_db_123`), `spring.jpa.hibernate.ddl-auto=update`, mail (SMTP Gmail), JWT secret/expiration, upload dir `C:/uploads/`, CORS origins.
- `frontend/src/main/resources/application.properties` — port 8080, `backend.base-url=http://localhost:8081`.

## Tests

- Backend tests: `backend/src/test/java/com/alamin/ecommerce/` (context, service, controller tests).
- Frontend tests: `frontend/src/test/java/com/alamin/ecommerce/frontend/` (context test, client contract deserialization test).
- No dedicated test runner beyond Maven Surefire; use `-Dtest=<ClassName>[#method]` to scope.

## What to avoid

- Do not list every file or component — the structure above is discoverable from the module source trees.
- Do not add generic development advice; this file covers only repo-specific commands, architecture, and boundaries.