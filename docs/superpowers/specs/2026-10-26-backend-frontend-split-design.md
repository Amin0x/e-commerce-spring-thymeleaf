# Split the monolith into an API backend and a Thymeleaf frontend

- **Date:** 2026-10-26
- **Status:** Approved design
- **Branch:** `feature/split-backend-frontend`

## Problem

The repository is a single Spring Boot application that serves HTML pages *and* owns the
database. `pom.xml` depends on `spring-boot-starter-thymeleaf`, `spring-boot-starter-data-jpa`,
`mysql-connector-j`, `spring-boot-starter-mail` and `itextpdf` all at once. Auth is
session-based `formLogin` with `rememberMe` (`config/SecurityConfig.java`), and
`CustomUserDetailsService` loads users straight from `tbl_users`.

We want two independently deployable applications in two subfolders of this repository, with
git staying at the repository root:

- `backend/` — REST API only, secured with Spring Security + JWT.
- `frontend/` — Thymeleaf server-rendered UI.

## Goals

1. Two Maven projects, `backend/` and `frontend/`, each producing its own runnable jar.
2. The backend speaks JSON only. No Thymeleaf, no template rendering, no session state.
3. The backend authenticates with Spring Security using JWT access tokens.
4. The frontend keeps its existing URLs and templates; a browser never sees a JWT.
5. The database, its schema and its seed data belong to the backend alone.
6. Git history is preserved at the repository root — the split is a move, not a re-create.

## Non-goals

These are deliberate exclusions, not oversights. Each has a stated cost.

| Excluded | Why | Cost of adding later |
|---|---|---|
| Refresh tokens and server-side token revocation | The access token lives 24 h and logout drops the frontend session. A revocation list is only needed if you must invalidate a token before it expires. | Add a `typ=refresh` claim, a `jti` denylist cache, and a `POST /api/auth/refresh` endpoint. |
| API versioning (`/api/v1/...`) | No external consumers exist yet, so `v1` would be a lie. | Prefix the `@RequestMapping` values. |
| OpenAPI / Swagger UI | Adds a dependency and a UI to maintain, orthogonal to the split. | Add `springdoc-openapi-starter-webmvc-ui`. |
| Making the frontend a security boundary | The frontend re-renders what the backend returns; every rule is enforced in the backend. | n/a — this is the intended design. |
| A shared `common/` Maven module | The brief was two subfolders. A third module would couple the two apps' build and release cycles. | `common/pom.xml` + a dependency in both children. |

## Repository layout

```
e-commerce-spring-thymeleaf/            <- git root
├── pom.xml                              <- aggregator
├── backend/
│   ├── pom.xml
│   ├── db/                              <- schema.sql, data.sql (reference only, not on classpath)
│   └── src/main/
│       ├── java/com/alamin/ecommerce/
│       └── resources/application.properties
├── frontend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/alamin/ecommerce/frontend/
│       └── resources/
│           ├── templates/               <- moved verbatim
│           ├── static/                  <- moved verbatim
│           └── messages*.properties      <- moved verbatim
└── docs/superpowers/specs/
```

The root `pom.xml` keeps `spring-boot-starter-parent:3.3.5` as its parent so both children
inherit Java 21 and Spring's dependency management, and declares:

```xml
<modules>
    <module>backend</module>
    <module>frontend</module>
</modules>
```

Each child declares `spring-boot-maven-plugin` itself so its `repackage` goal produces a runnable
jar. `mvn package` at the root builds both; `mvn -pl backend package` builds one.

The root `Dockerfile` is replaced by `backend/Dockerfile` and `frontend/Dockerfile`.

`schema.sql` and `data.sql` currently live at the repository root. They are **not** on the
classpath, and Spring Boot does not auto-run them against MySQL (`spring.sql.init.mode` defaults to
`embedded`). They therefore act as reference scripts today. They move to `backend/db/` — deliberately
**not** to `backend/src/main/resources/`, because putting them on the classpath would silently
change them from reference scripts into scripts that run at every boot.

## Backend design

Package root stays `com.alamin.ecommerce` so the existing imports need no rewriting.

```
com.alamin.ecommerce
├── EcommerceBackendApplication.java
├── config/
│   ├── SecurityConfig.java              <- stateless resource server, CORS, JwtDecoder/Encoder
│   ├── CorsConfig.java
│   └── TaskSchedule.java                <- moved from the old config package
├── security/
│   ├── JwtService.java                  <- issue / validate tokens
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationConverter.java  <- role claim -> bare authority
│   ├── CustomUserDetailsService.java    <- moved from the old config package
│   ├── RestAuthenticationEntryPoint.java
│   └── RestAccessDeniedHandler.java
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   └── dto/  LoginRequest, LoginResponse, RegisterRequest, AuthUserResponse
├── dto/                                 <- shared response contracts
├── common/  Page, PageBuilder           <- moved verbatim
├── exception/                           <- @RestControllerAdvice, ApiError
└── user/ cart/ category/ checkout/ order/ product/ invoice/
    settings/ statistics/ stock/ subscribe/
    - entities, repositories, services    <- moved verbatim
    - *RestController                     <- new
```

### Spring Security + JWT

Dependencies: `spring-boot-starter-oauth2-resource-server` and `spring-security-oauth2-jose`
(Nimbus JOSE + JWT). Signed with HMAC-SHA256.

- `SessionCreationPolicy.STATELESS`. CSRF disabled (the token travels in a header, not a cookie).
- Secret from `app.jwt.secret`, overridable by the `JWT_SECRET` environment variable. Must be
  at least 256 bits; the application fails fast at startup if it is not.
- `NimbusJwtEncoder` over an `ImmutableSecret`; `NimbusJwtDecoder.withSecretKey(...).macAlgorithm(HS256)`.
- Token claims: `sub` = username, `role` = `USER` | `ADMIN` | `MODERATOR`, `uid` = user uuid,
  `iat`, `exp` = now + 24 h, `jti`.
- `JwtAuthenticationConverter` maps the `role` claim to a **bare** `SimpleGrantedAuthority(role)`.
  The existing code authorizes with `hasAuthority("ADMIN")` — not `ROLE_ADMIN` — so this preserves
  the current authority strings exactly and no access rule has to be rewritten.
- Authentication failures return JSON (`401` / `403`), not a redirect to a login page.

### API surface

| Group | Endpoints | Access |
|---|---|---|
| Auth | `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/logout`, `GET /api/auth/me` | login/register public; me/logout authenticated |
| Catalog | `GET /api/products` (paged, filterable), `GET /api/products/{id}`, `GET /api/products/bestselling`, `GET /api/categories`, `GET /api/categories/{idOrSlug}`, `GET /api/categories/random`, `GET /api/categories/top` | public |
| Cart | `GET /api/cart`, `POST /api/cart/items`, `DELETE /api/cart/items/{id}`, `POST /api/cart/items/{id}/increment`, `POST /api/cart/items/{id}/decrement` | authenticated; guests via `X-Cart-Key` |
| Orders | `GET /api/orders`, `POST /api/orders`, `GET /api/orders/{id}`, `GET /api/invoices/{orderId}` (PDF bytes) | authenticated |
| Settings | `GET /api/settings/{name}`, `GET /api/settings` | public |
| Statistics | `GET /api/statistics`, `POST /api/statistics` | GET public, POST admin |
| Subscription | `POST /api/subscribe`, `GET /api/subscribe/verify`, `GET /api/subscribe/unsubscribe` | public |
| Admin | `/api/admin/products/**`, `/api/admin/categories/**`, `/api/admin/users/**`, `/api/admin/orders/**`, `/api/admin/carts/**`, `/api/admin/settings/**`, `/api/admin/statistics/**`, `/api/admin/address/**`, `POST /api/admin/upload`, `POST /api/admin/products/images/upload` | `ADMIN` |
| Files | `GET /api/files/{name}` | public |

The application's existing `/admin/api/**` paths collapse into a single `/api/admin/**` prefix.
There is no second `/api` namespace.

`GET /api/categories/{idOrSlug}` resolves a numeric segment as a primary key first and falls back
to a slug. This replaces a defective pair of mappings in the current code — see
[Pre-existing defects carried into the split](#pre-existing-defects-carried-into-the-split).

### Cart identity without sessions

`CartServiceImpl` currently takes `HttpSession` and `Principal` and keys guest carts by
`session.getId()` (`cart/CartRepository.getCartBySession`). A stateless JWT backend cannot see an
HTTP session, so the service signatures change:

```java
// before
Cart addCartItem(Product item, HttpSession session, Principal principal);

// after
Cart addCartItem(Product item, String cartKey, String username);
```

- `username` is non-null for a logged-in caller and comes from the JWT `sub` claim.
- `cartKey` identifies a guest. The frontend sends its own `HttpSession` id in the `X-Cart-Key`
  header; the backend stores it in the existing `Cart.sessionId` column.
- Resolution order is unchanged: authenticated user by `userId`, otherwise by `cartKey`.

### Errors

`GlobalExceptionHandler` becomes a `@RestControllerAdvice` producing:

```json
{
  "timestamp": "2026-10-26T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input",
  "path": "/api/admin/products",
  "fieldErrors": { "name": "must not be blank" }
}
```

The Thymeleaf-flavoured `MyCustomErrorControllerWithAttrsJson` is deleted from the backend.

### Files and uploads

`FileUploadService` currently hardcodes `"C:\\uploads\\"`. The directory becomes
`app.upload.dir`, defaulting to `C:/uploads/` on Windows. The backend serves uploaded files at
`GET /api/files/{name}`, replacing the current `/admin/images/{name}` and `/uploads/images/{name}`
mappings.

### Email

`spring-boot-starter-mail` moves to the backend. The subscribe and confirmation flows send mail
from the backend; the frontend calls the API endpoints.

## Frontend design

Package root is `com.alamin.ecommerce.frontend`.

```
com.alamin.ecommerce.frontend
├── FrontendApplication.java
├── config/
│   ├── SecurityConfig.java              <- session based, role rules
│   ├── RestClientConfig.java            <- RestTemplate + auth interceptor
│   └── WebMvcConfig.java
├── security/
│   ├── SessionAuthenticationFilter.java <- rebuilds SecurityContext from the session
│   └── FrontendPrincipal.java
├── client/
│   ├── ApiClientSupport.java            <- shared error handling
│   ├── AuthApiClient, ProductApiClient, CategoryApiClient, CartApiClient,
│   │   OrderApiClient, UserApiClient, SettingsApiClient, StatisticsApiClient,
│   │   SubscribeApiClient, InvoiceApiClient, FileApiClient
│   └── dto/                             <- mirrored contracts
├── controller/
│   ├── HomeController, ProductController, CategoryController, CartController,
│   │   CheckoutController, SubscribeController, AuthController,
│   │   AdminHomeController, AdminProductController, AdminCategoryController,
│   │   AdminUserController, AdminOrderController, AdminCartController,
│   │   AdminSettingController, AdminAddressController, FileController
├── dto/                                 <- Page, PageBuilder, form-backing objects
└── exception/                           <- error handling for the HTML surface
```

Dependencies kept: `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`,
`spring-boot-starter-security`, `spring-boot-starter-validation`,
`htmx-spring-boot-thymeleaf`.
Dependencies dropped: `spring-boot-starter-data-jpa`, `mysql-connector-j`,
`spring-boot-starter-mail`, `itextpdf`, `spring-boot-starter-webflux`,
`spring-boot-starter-actuator`. (`webflux` is dropped because nothing in the codebase uses
`WebClient` or `Mono`; the existing `RestTemplate` bean already covers the client need.)

### How the frontend authenticates

The browser never sees a JWT.

1. `POST /auth/login` on the frontend → the frontend calls `POST /api/auth/login` on the backend.
2. The backend returns `{ "token": "...", "user": { "username", "role", "uuid", "firstName",
   "lastName", "email" } }`.
3. The frontend stores the token and the user summary in its `HttpSession`.
4. `SessionAuthenticationFilter` rebuilds the `SecurityContext` from those session attributes on
   each request, so `sec:authorize`, form logout and the rest of the existing security setup keep
   working.
5. `ClientAuthInterceptor` adds `Authorization: Bearer <token>` to every backend call.
6. On a `401` from the backend, the frontend clears the session and redirects to `/auth/login`.

**The frontend does not validate the JWT.** It has no signing key and performs no cryptography.
The frontend session is populated only after the backend has authenticated the user, and the
frontend's role in the authorization model is only to decide which pages to render. The backend
independently enforces every rule on every request, so a stale or forged role in the frontend
session can never grant access. The trade-off is that a role change in the database takes effect on
the user's next login rather than immediately — acceptable, and the backend still blocks the stale
role from acting.

### What changes in the templates

As little as possible. The templates post to frontend paths such as `@{/admin/users/{id}}` and
`@{/carts/getCart}`, and read model attributes such as `${user}` (a form-backing object, not the
logged-in user). Both survive the split unchanged.

The exceptions are URLs pointing at **uploaded** images (product, category and avatar images), which
now live on the backend. Those `th:src` attributes become absolute using a Thymeleaf property:

```html
<img th:src="@{${backend.base-url} + '/api/files/' + ${product.image}}">
```

### Shared contracts

The backend owns the contracts. The frontend mirrors them as its own DTO classes so that both
projects stay independently deployable with no shared jar. The backend's DTOs are the source of
truth.

Drift between the two sides is caught by a **committed JSON fixture** per contract:

- A backend test serialises each DTO to JSON and compares it against a checked-in fixture at
  `backend/src/test/resources/contracts/<Dto>.json`. The test fails if the backend's DTO changes
  and the fixture is not updated, which makes the contract change explicit.
- A frontend test deserialises the *same* fixture — copied to
  `frontend/src/test/resources/contracts/<Dto>.json` — into the mirrored DTO and asserts the
  expected fields are populated. It fails if the frontend's mirror drifts from the shape.

The fixture is the shared artefact; the code is not. Regenerating it is a deliberate, visible step.

## Data flow

```
Login   browser --form--> frontend /auth/login --POST /api/auth/login--> backend
                                    <-- {token, user{...}} --
                         session stores token + user summary; redirect

Page    browser --GET--> frontend controller --GET /api/products--> backend
                                    <-- ProductDto[] --
                         model --> Thymeleaf --> HTML

Write   browser --form--> frontend controller --POST /api/admin/products--> backend
                                    <-- 201 / 400 --
                         redirect, or render the error page

Session frontend filter reads the session --> SecurityContext --> sec:authorize works
         backend enforces the real rule on every request
```

## Configuration

| Property | Backend | Frontend |
|---|---|---|
| `server.port` | `8081` | `8080` |
| `app.jwt.secret` | required, ≥256 bit, `JWT_SECRET` env override | **absent** — the key never leaves the backend |
| `app.jwt.access-token-expiration` | `24h` | — |
| `app.upload.dir` | `C:/uploads/` | — |
| `app.cors.allowed-origins` | `http://localhost:8080` | — |
| `backend.base-url` | — | `http://localhost:8081` |
| `spring.datasource.*` | unchanged | **absent** |

## Testing

The repository currently has no tests. This split adds them.

**Backend** — `MockMvc` plus `spring-security-test`'s `jwt()` request post-processor:

- JWT issue / validate / expire round-trip, and the `role` → authority mapping.
- Every authorisation rule: public endpoints reachable, `USER` endpoints reject `USER`-less and
  `ADMIN`-only callers with the right status.
- 401 and 403 bodies are JSON.
- One happy path and one failure path per domain controller.

**Frontend** — `MockMvc` with `MockRestServiceServer` bound to the `RestTemplate`:

- The controller issues the expected backend call with the expected path, query and body.
- The backend response is mapped into the model the template expects.
- The session filter populates the `SecurityContext`, and a backend `401` clears the session and
  redirects to the login page.

Both projects keep `spring-boot-starter-test`.

## Pre-existing defects carried into the split

The split forces these to be resolved rather than carried over verbatim. Each is a genuine defect
in the current code, not a consequence of the split.

| Defect | Where | Resolution |
|---|---|---|
| Two conflicting mappings on the same path: `@GetMapping("/{id}")` binds a `Long`, `@GetMapping("/{slug}")` binds a `String`. Spring breaks the tie on the pattern string, so `{id}` always wins and the slug mapping is unreachable. Its `@PathVariable String id` also does not match its own `{slug}` placeholder. | `category/CategoryController.java:50,64` | One `GET /api/categories/{idOrSlug}` endpoint that tries the primary key when the segment is numeric and falls back to a slug. |
| `CartServiceImpl` reads `HttpSession` directly from the service layer. | `cart/CartServiceImpl.java:37,55,104,159,182` | Replaced by the `cartKey` / `username` parameters described above. |
| `UserServiceImpl.createUser(User)` never sets `created`, which is `@Column(nullable = false)` — so the admin user-create path violates a NOT NULL constraint. It also ignores the uploaded avatar, and silently downgrades any role that is not `USER`/`ADMIN`/`MODERATOR`. | `user/UserServiceImpl.java:93`; `user/AdminUserController.java:112` | Set `created`/`updated` on every write path, persist the uploaded avatar, and reject an unknown role with a validation error instead of silently substituting `USER`. |
| `UserServiceImpl.sendConformationEmail` builds a `SimpleMailMessage` inside a plain-text block and never sends it — the `emailSender.send(message)` call is commented out. | `user/UserServiceImpl.java:179` | The mail sending lives in the backend; the endpoint calls the mail sender for real. |
| `FileUploadService` hardcodes `"C:\\uploads\\"` in two methods and has a third that writes to a relative `uploads/` path. | `config/FileUploadService.java:24,49,69` | One configurable `app.upload.dir`, used by all three. |
| `SecurityConfig` lists `.requestMatchers("/**").permitAll()` *before* the `/admin/**` and `/user/**` rules. The first matching rule wins, so the admin and user rules are unreachable. | `config/SecurityConfig.java:35-37` | Most-specific rules first in both applications. |

## Implementation phases

Work is ordered so that each phase leaves the repository compiling and, where possible, runnable.

1. **Scaffolding** — root aggregator pom, `backend/` and `frontend/` poms, branch and move
   `templates/`, `static/`, `messages*.properties` with `git mv` so history is preserved.
2. **Backend foundation** — move entities, repositories, services and DTOs; add the JWT security
   configuration, the auth endpoints, and the JSON exception handler.
3. **Backend REST surface** — one `@RestController` per domain, matching the endpoint table.
4. **Frontend client layer** — mirrored DTOs, `RestTemplate` configuration, the auth interceptor and
   the per-domain API clients.
5. **Frontend controllers and security** — rewrite the 20 controllers against the clients, add the
   session filter and the frontend `SecurityConfig`, rewire login and logout.
6. **Wiring and cleanup** — ports, CORS, upload URLs, root `Dockerfile` split, `.gitignore`,
   the GitHub Actions workflow, and the README.

## Risks

| Risk | Mitigation |
|---|---|
| Template breakage from a changed model attribute | The frontend contract test asserts the model keys the templates read. Templates are moved verbatim and only re-pointed at uploaded-image URLs. |
| The frontend's stale role grants a page it should not show | The backend enforces every rule independently. A rendered page the user may not use is a cosmetic issue, not a security one. |
| A shared-key mismatch between the two applications | The frontend has no key. The only shared configuration value is `backend.base-url`. |
| Guest carts losing their contents | The `X-Cart-Key` mechanism preserves the existing `session.getId()` keying exactly. |
| Unbounded growth of the endpoint table | The table is enumerated in this spec; anything outside it is out of scope for this split. |
