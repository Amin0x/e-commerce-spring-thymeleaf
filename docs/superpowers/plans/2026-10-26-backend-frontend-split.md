# Backend/Frontend Split Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Split the monolithic Spring Boot + Thymeleaf e-commerce application into two independently deployable Maven subprojects — `backend/` (REST API only, secured with Spring Security + JWT) and `frontend/` (Thymeleaf server-rendered UI) — as subfolders of the repository root, with git history preserved at root.

**Architecture:** Root aggregator `pom.xml` declares `backend` and `frontend` modules. `backend/` keeps package `com.alamin.ecommerce`, owns the database, entities, repositories, services, JWT resource-server security, and one `@RestController` per domain. `frontend/` uses package `com.alamin.ecommerce.frontend`, keeps templates/static/messages verbatim (via `git mv`), mirrors DTOs, calls the backend through a `RestTemplate` per domain with a `ClientAuthInterceptor` that pulls the JWT from the frontend `HttpSession`, and rebuilds its `SecurityContext` via a `SessionAuthenticationFilter` so `sec:authorize` and the existing HTML surface keep working.

**Tech Stack:** Spring Boot 3.3.5, Java 21, MySQL, `mysql-connector-j` 8.3.0, Thymeleaf, `spring-boot-starter-oauth2-resource-server` + `spring-security-oauth2-jose` (Nimbus, HMAC-SHA256) on the backend, `RestTemplate` on the frontend, MockMvc + `jwt()` for backend tests, MockRestServiceServer for frontend tests, `spring-boot-starter-parent:3.3.5`.

**Spec:** `docs/superpowers/specs/2026-10-26-backend-frontend-split-design.md` — the plan argues from the spec, so the spec travels with it; executors read both.

## Global Constraints

(One line each, copied verbatim from the spec; every task's requirements implicitly include this section.)

- Spring Boot 3.3.5, Java 21 (`<java.version>21</java.version>`).
- Root aggregator pom: parent `spring-boot-starter-parent:3.3.5`, `groupId=com.alamin`, `artifactId=ecommerce`, version `0.0.1-SNAPSHOT`.
- `backend/` port `8081`; `frontend/` port `8080`; CORS allowed origin `http://localhost:8080`.
- Backend owns `app.jwt.secret` (required, ≥256 bit, overridable by `JWT_SECRET` env var); backend fails fast at startup if the secret is < 256 bits.
- Backend owns `app.upload.dir` (default `C:/uploads/`).
- Backend owns `spring.datasource.*`; frontend must not declare a datasource.
- Frontend owns `backend.base-url` (`http://localhost:8081`); frontend must NOT declare `app.jwt.secret`.
- JWT: HS256, 24h expiry, claims `sub`=username, `role` (USER|ADMIN|MODERATOR), `uid`=uuid, `iat`, `exp`, `jti`; `SessionCreationPolicy.STATELESS`; CSRF disabled.
- `JwtAuthenticationConverter` maps `role` claim to `SimpleGrantedAuthority(role)` (bare authority, no `ROLE_` prefix).
- Backend authenticates with `spring-boot-starter-oauth2-resource-server` + `spring-security-oauth2-jose`; 401/403 return JSON, never a login-page redirect.
- Frontend keeps `sec:authorize`, form logout, and existing template URLs; browser never sees the JWT; frontend performs no JWT crypto (no signing key).
- Frontend login calls backend `POST /api/auth/login`, stores `{token,user}` in its `HttpSession`; `ClientAuthInterceptor` adds `Authorization: Bearer <token>` to every backend call; backend `401` clears the frontend session and redirects to `/auth/login`.
- Two Maven subprojects, each producing its own runnable jar; `spring-boot-maven-plugin` declared in each child so `repackage` runs; `mvn package` at root builds both; `mvn -pl backend package` builds one.
- Root `pom.xml` declares `<modules>backend</module><module>frontend</module>`.
- `schema.sql`/`data.sql` move to `backend/db/` as reference only — never on the classpath.
- Root `Dockerfile` replaced by `backend/Dockerfile` (port 8081) and `frontend/Dockerfile` (port 8080).
- `spring-boot-starter-webflux`, `spring-boot-starter-actuator`, `spring-boot-starter-mail`, `itextpdf`, `mysql-connector-j`, `spring-boot-starter-data-jpa` are dropped from the frontend pom.
- Frontend keeps `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `htmx-spring-boot-thymeleaf` 3.5.0.
- DTO sharing = duplicated mirrored DTOs per project, no shared `common` module; contract = a committed JSON fixture per contract (`backend/src/test/resources/contracts/<Dto>.json` copied to frontend).
- Git stays at the repository root; `templates/`, `static/`, `messages*.properties` moved with `git mv` so history is preserved.
- Most-specific security rules first (avoid the pre-existing `/**` permitAll shadowing bug).
- API scope: full coverage per the spec endpoint table (~60-80 endpoints across all 15 domain packages).
- Pre-existing defects fixed in-split rather than carried over: conflicting `/api/categories/{id}`/`{slug}` mappings merged into `{idOrSlug}`; `CartServiceImpl` signature changed from `(HttpSession, Principal)` to `(cartKey, username)`; `createUser(User)` sets `created`/`updated`, persists avatar, rejects unknown role; `sendConformationEmail` sends for real; `FileUploadService` uses configurable `app.upload.dir`.
- No platform shared module, no refresh tokens, no API versioning, no OpenAPI, frontend is not a security boundary.

---

# Phase 1: Scaffolding

**Deliverable:** Root aggregator `pom.xml`, `backend/pom.xml`, `frontend/pom.xml`, both applications start (empty context loads), templates/static/messages moved with `git mv`, `schema.sql`/`data.sql` in `backend/db/`, two Dockerfiles, and everything committed so `mvn package` at root builds both modules.

## Task 1: Root aggregator pom.xml

**Files:**
- Modify: `pom.xml` (root)

**Interfaces:**
- Consumes: none (this is the project entry point).
- Produces: aggregator pom with `<modules>backend</module><module>frontend</module>` and no direct runtime dependencies (dependencies move into child poms).

- [ ] **Step 1: Rewrite root pom.xml as aggregator**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>
    <groupId>com.alamin</groupId>
    <artifactId>ecommerce</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>ecommerce</name>
    <description>e-commerce project for Spring</description>
    <modules>
        <module>backend</module>
        <module>frontend</module>
    </modules>
    <properties>
        <java.version>21</java.version>
    </properties>
</project>
```

Remove every `<dependency>` and `<plugin>` block from the old root pom — all dependencies are declared in `backend/pom.xml` and `frontend/pom.xml`.

- [ ] **Step 2: Verify root aggregator resolves**

Run: `mvn -q validate` at the repository root.
Expected: BUILD SUCCESS, no "Non-resolvable parent POM" errors.

- [ ] **Step 3: Commit**

```bash
git add pom.xml
git commit -m "refactor: make root pom an aggregator for backend and frontend modules"
git log --oneline -1
```

## Task 2: backend/pom.xml

**Files:**
- Create: `backend/pom.xml`

**Interfaces:**
- Consumes: none.
- Produces: backend module descriptor securing the API; declares `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-mail`, `spring-boot-starter-oauth2-resource-server`, `spring-security-oauth2-jose`, `mysql-connector-j`, `lombok` (optional), `spring-boot-starter-test` (test), and `spring-boot-maven-plugin`.

- [ ] **Step 1: Create backend/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.alamin</groupId>
        <artifactId>ecommerce</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <artifactId>backend</artifactId>
    <name>backend</name>
    <description>REST API backend for e-commerce (JWT secured)</description>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security.oauth.boot</groupId>
            <artifactId>spring-security-oauth2-jose</artifactId>
            <version>3.3.5</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
        <finalName>backend</finalName>
    </build>
</project>
```

Note: `spring-security-oauth2-jose` is the artifact providing `NimbusJwtEncoder`/`NimbusJwtDecoder`; version pinned to `3.3.5` to match the Spring Boot parent.

- [ ] **Step 2: Compile backend module (empty source tree OK for now)**

Run: `mvn -pl backend -q compile`
Expected: BUILD SUCCESS (no source yet, that is fine).

- [ ] **Step 3: Commit**

```bash
git add backend/pom.xml
git commit -m "build: add backend module pom with JWT resource-server deps"
```

## Task 3: frontend/pom.xml

**Files:**
- Create: `frontend/pom.xml`

**Interfaces:**
- Consumes: none.
- Produces: frontend module descriptor; declares `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `htmx-spring-boot-thymeleaf` 3.5.0, `lombok` (optional), `spring-boot-starter-test` (test). No `data-jpa`, `mysql-connector-j`, `mail`, `itextpdf`, `webflux`, or `actuator`.

- [ ] **Step 1: Create frontend/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.alamin</groupId>
        <artifactId>ecommerce</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <artifactId>frontend</artifactId>
    <name>frontend</name>
    <description>Thymeleaf server-rendered UI for e-commerce</description>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.wimdeblauwe</groupId>
            <artifactId>htmx-spring-boot-thymeleaf</artifactId>
            <version>3.5.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
        <finalName>frontend</finalName>
    </build>
</project>
```

- [ ] **Step 2: Compile frontend module**

Run: `mvn -pl frontend -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
git add frontend/pom.xml
git commit -m "build: add frontend module pom with Thymeleaf and security deps"
```

## Task 4: Backend application main class

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/EcommerceBackendApplication.java`

**Interfaces:**
- Consumes: none.
- Produces: `@SpringBootApplication(scanBasePackages = "com.alamin.ecommerce")` entry point for the backend so component scans exactly the existing `com.alamin.ecommerce` package tree.

- [ ] **Step 1: Create EcommerceBackendApplication.java**

```java
package com.alamin.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.alamin.ecommerce")
public class EcommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceBackendApplication.class, args);
    }

}
```

- [ ] **Step 2: Create backend test placeholder and run**

Create `backend/src/test/java/com/alamin/ecommerce/BackendContextTest.java`:

```java
package com.alamin.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EcommerceBackendApplication.class)
class BackendContextTest {

    @Test
    void contextLoads() {
    }

}
```

Run: `mvn -pl backend -q test -Dtest=BackendContextTest`
Expected: BUILD SUCCESS, test passes.

- [ ] **Step 3: Commit**

```bash
git add backend/src
git commit -m "feat: add backend application main class and context test"
```

## Task 5: Frontend application main class

**Files:**
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/FrontendApplication.java`

**Interfaces:**
- Consumes: none.
- Produces: `@SpringBootApplication(scanBasePackages = "com.alamin.ecommerce.frontend")` entry point for the frontend so component scans exactly `com.alamin.ecommerce.frontend` and does NOT pick up `com.alamin.ecommerce` backend classes.

- [ ] **Step 1: Create FrontendApplication.java**

```java
package com.alamin.ecommerce.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.alamin.ecommerce.frontend")
public class FrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

}
```

- [ ] **Step 2: Create frontend context test and run**

Create `frontend/src/test/java/com/alamin/ecommerce/frontend/FrontendContextTest.java`:

```java
package com.alamin.ecommerce.frontend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = FrontendApplication.class)
class FrontendContextTest {

    @Test
    void contextLoads() {
    }

}
```

Run: `mvn -pl frontend -q test -Dtest=FrontendContextTest`
Expected: BUILD SUCCESS, test passes.

- [ ] **Step 3: Commit**

```bash
git add frontend/src
git commit -m "feat: add frontend application main class and context test"
```

## Task 6: Move templates/static/messages to frontend via git mv

**Files:**
- Modify: `src/main/resources/templates/` → `frontend/src/main/resources/templates/` (all files)
- Modify: `src/main/resources/static/` → `frontend/src/main/resources/static/` (all files)
- Modify: `src/main/resources/messages*.properties` → `frontend/src/main/resources/` (if any exist at root resources)

**Interfaces:**
- Consumes: the existing template/resource tree at `src/main/resources/`.
- Produces: the same files now living under `frontend/src/main/resources/` with git history preserved.

- [ ] **Step 1: Move templates, static, messages with git mv**

Run:
```bash
git mv src/main/resources/templates frontend/src/main/resources/templates
git mv src/main/resources/static frontend/src/main/resources/static
git mv src/main/resources/messages*.properties frontend/src/main/resources/ 2>/dev/null || true
git status --short
```

Verify `frontend/src/main/resources/templates` contains `public/` and `admin/` subtrees.

- [ ] **Step 2: Remove now-empty src/main/resources directory tree**

Run: `rmdir src/main/resources 2>/dev/null || true` — only if empty.

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "refactor: move templates, static and messages to frontend module via git mv"
grep -R "th:href\|th:src" frontend/src/main/resources/templates | head -5
```

## Task 7: backend/db/ with schema.sql and data.sql as reference only

**Files:**
- Create: `backend/db/schema.sql`
- Create: `backend/db/data.sql`
- Modify: root `schema.sql` and `data.sql` remain at root (verbatim copy) OR move — per spec they move to `backend/db/` and are NOT placed on the classpath.

**Interfaces:**
- Consumes: existing root `schema.sql` (5.3KB) and `data.sql` (49.7KB).
- Produces: `backend/db/schema.sql` and `backend/db/data.sql`, reference scripts, deliberately not under `backend/src/main/resources/` so Spring Boot never auto-runs them at boot.

- [ ] **Step 1: Move schema.sql and data.sql to backend/db/**

Run:
```bash
mkdir backend/db
git mv schema.sql backend/db/schema.sql
git mv data.sql backend/db/data.sql
ls -la backend/db/
```

- [ ] **Step 2: Verify they are not on any classpath**

Confirm `backend/src/main/resources` has no `schema.sql`/`data.sql`.
Run: `find backend/src -name "schema.sql" -o -name "data.sql"` → should print nothing.

- [ ] **Step 3: Commit**

```bash
git add backend/db
git commit -m "refactor: move schema.sql and data.sql to backend/db as reference only"
```

## Task 8: backend application.properties

**Files:**
- Create: `backend/src/main/resources/application.properties`

**Interfaces:**
- Consumes: none.
- Produces: backend config with `server.port=8081`, datasource unchanged, JPA `ddl-auto=update`, mail, JWT secret placeholder, upload dir, CORS origin.

- [ ] **Step 1: Create backend/src/main/resources/application.properties**

```properties
# Backend — REST API only, port 8081
spring.application.name=backend
server.port=8081
server.error.whitelabel.enabled=false

# Database (unchanged from monolith)
spring.datasource.url=jdbc:mysql://localhost:3306/amin_db_123
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Mail (subscribe / confirmation flows live in the backend)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=garogigi@gmail.com
spring.mail.password=your-email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT (HS256, 24h expiry). app.jwt.secret must be >= 256 bits; overridable via JWT_SECRET env var.
app.jwt.secret=change-me-to-a-secret-at-least-256-bits-long
app.jwt.access-token-expiration=86400000

# Uploads
app.upload.dir=C:/uploads/

# CORS — frontend origin only
app.cors.allowed-origins=http://localhost:8080

logging.level.org.springframework=DEBUG
logging.level.org.hibernate=DEBUG
```

- [ ] **Step 2: Start backend and confirm it binds to 8081**

Run: `mvn -pl backend -q spring-boot:run` (background), wait for "Started EcommerceBackendApplication", then hit `http://localhost:8081/actuator/health` is not present (no actuator) — instead confirm the process starts and exposes a 401 on a protected path. Stop the process with Ctrl+C.
Expected: application starts on port 8081 without "Parameter 0 of method ... dataSource" failures.

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/application.properties
git commit -m "config: backend application.properties (port 8081, datasource, jwt, uploads)"
```

## Task 9: frontend application.properties

**Files:**
- Create: `frontend/src/main/resources/application.properties`

**Interfaces:**
- Consumes: none.
- Produces: frontend config with `server.port=8080`, `backend.base-url=http://localhost:8081`, no datasource, no `app.jwt.secret`.

- [ ] **Step 1: Create frontend/src/main/resources/application.properties**

```properties
# Frontend — Thymeleaf UI, port 8080
spring.application.name=frontend
server.port=8080
server.error.whitelabel.enabled=false

# Backend API base URL used by templates for uploaded-image URLs and by RestTemplate clients
backend.base-url=http://localhost:8081

logging.level.org.springframework=DEBUG
```

- [ ] **Step 2: Start frontend and confirm it binds to 8080**

Run: `mvn -pl frontend -q spring-boot:run` (background), confirm startup on port 8080, stop with Ctrl+C.

- [ ] **Step 3: Commit**

```bash
git add frontend/src/main/resources/application.properties
git commit -m "config: frontend application.properties (port 8080, backend.base-url)"
```

## Task 10: Dockerfiles per module

**Files:**
- Create: `backend/Dockerfile`
- Create: `frontend/Dockerfile`
- Modify: `Dockerfile` (root) — delete or repurpose; spec says root Dockerfile is replaced by the two module Dockerfiles.

**Interfaces:**
- Consumes: root `Dockerfile` (805B) as a template.
- Produces: `backend/Dockerfile` exposing 8081, `frontend/Dockerfile` exposing 8080.

- [ ] **Step 1: Create backend/Dockerfile**

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml /app/backend/pom.xml
COPY pom.xml /app/pom.xml
RUN mvn -f /app/pom.xml -pl backend dependency:go-offline -B
COPY backend/src /app/backend/src
RUN mvn -f /app/pom.xml -pl backend package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/target/backend.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]
```

- [ ] **Step 2: Create frontend/Dockerfile**

Same as backend but `-pl frontend`, copies `frontend/target/frontend.jar`, exposes `8080`.

- [ ] **Step 3: Remove root Dockerfile and commit**

Run: `git rm Dockerfile` then commit both new Dockerfiles + removal.

```bash
git add backend/Dockerfile frontend/Dockerfile
git rm Dockerfile
git commit -m "build: split root Dockerfile into backend/Dockerfile and frontend/Dockerfile"
```

## Task 11: .gitignore and workflow wiring

**Files:**
- Modify: `.gitignore`
- Modify: `.github/workflows/maven-publish.yml` (module-aware build)

**Interfaces:**
- Consumes: existing `.gitignore` (`/target/`).
- Produces: `.gitignore` covering both module `target/` dirs; workflow builds with `mvn -B package --file pom.xml` (inherently builds both modules).

- [ ] **Step 1: Update .gitignore**

Append:
```
/target/
backend/target/
frontend/target/
*.iml
.idea/
```

- [ ] **Step 2: Commit scaffold**

```bash
git add -A
git commit -m "build: complete split scaffold — root aggregator + two modules, both compile and start"
git status --short   # expect clean
mvn -q package -DskipTests -pl backend,frontend
```

Expected: both modules package into `backend.jar` and `frontend.jar`.

---

# Phase 2: Backend foundation

**Deliverable:** Backend owns entities, repositories, services, DTOs, `common/Page`, exceptions, JWT security (`SecurityConfig` stateless resource server, `JwtService`, `JwtAuthenticationFilter`, `JwtAuthenticationConverter`, `CustomUserDetailsService`, REST auth entrypoint/denied handlers), `AuthController` (`/api/auth/**`), and a JSON `@RestControllerAdvice`. Backend compiles and its security/auth tests pass.

## Task 12: Move entities, repositories, DTOs, and common to backend

**Files:**
- Modify: `git mv src/main/java/com/alamin/ecommerce/{cart,category,checkout,home,invoice,order,product,settings,statistics,stock,subscribe,user}/*.java backend/src/main/java/com/alamin/ecommerce/{...}/`
- Modify: `git mv src/main/java/com/alamin/ecommerce/common/*.java backend/src/main/java/com/alamin/ecommerce/common/`
- Modify: `git mv src/main/java/com/alamin/ecommerce/dto` — backend keeps `dto/` for shared response contracts (create `backend/src/main/java/com/alamin/ecommerce/dto/`).

**Interfaces:**
- Consumes: existing entity/repository/DTO classes under package `com.alamin.ecommerce.*`.
- Produces: same classes unchanged, now under `backend/src/main/java/com/alamin/ecommerce/` — package declarations stay `com.alamin.ecommerce`, so no import rewriting.

- [ ] **Step 1: git mv entity/repo/DTO/common trees into backend**

Run (one command, all `git mv`):
```bash
cd "C:\Users\AHMED\Desktop\e-commerce-spring-thymeleaf"
for d in cart category checkout home invoice order product settings statistics stock subscribe user common; do
  git mv "src/main/java/com/alamin/ecommerce/$d" "backend/src/main/java/com/alamin/ecommerce/$d"
done
mkdir -p backend/src/main/java/com/alamin/ecommerce/dto
find backend/src/main/java -name "*.java" | wc -l   # expect ~70+
```

- [ ] **Step 2: Compile backend with moved sources**

Run: `mvn -pl backend -q compile`
Expected: BUILD SUCCESS — no "package does not exist" errors since packages are unchanged.

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "refactor: move entities, repositories, DTOs and common to backend module"
```

## Task 13: Move services to backend; fix CartServiceImpl signature

**Files:**
- Modify: `git mv src/main/java/com/alamin/ecommerce/{cart,category,checkout,home,invoice,order,product,settings,statistics,stock,subscribe,user}/*Service*.java` already moved in Task 12 — take ownership now.
- Modify: `backend/src/main/java/com/alamin/ecommerce/cart/CartService.java` — change signature.
- Modify: `backend/src/main/java/com/alamin/ecommerce/cart/CartServiceImpl.java` — change signature + body.
- Modify: `backend/src/main/java/com/alamin/ecommerce/user/UserServiceImpl.java` — fix `createUser` (created/updated, avatar, role rejection).
- Modify: `backend/src/main/java/com/alamin/ecommerce/config/FileUploadService.java` — configurable `app.upload.dir`.

**Interfaces (CartService changes):**

Before (monolith):
```java
Cart addCartItem(Product item, HttpSession session, Principal principal);
Cart removeCartItem(Long id, HttpSession session, Principal principal);
Cart incrementCartItem(Long id, HttpSession session, Principal principal);
Cart decrementCartItem(Long id, HttpSession session, Principal principal);
Cart getCartBySession(HttpSession session);
```

After (backend, stateless):
```java
Cart getCartBySession(String cartKey);                      // guest cart key
Cart addCartItem(Product item, String cartKey, String username);
Cart removeCartItem(Long id, String cartKey, String username);
Cart incrementCartItem(Long id, String cartKey, String username);
Cart decrementCartItem(Long id, String cartKey, String username);
```

`username` is non-null for authenticated callers (JWT `sub`); `cartKey` identifies a guest (frontend sends its `HttpSession` id in `X-Cart-Key`). Resolution order unchanged: authenticated user by `userId`, otherwise by `cartKey`. `Cart.sessionId` column continues to hold `cartKey`.

- [ ] **Step 1: Write failing CartService stateless test**

Create `backend/src/test/java/com/alamin/ecommerce/cart/CartServiceStatelessTest.java`:

```java
package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartServiceStatelessTest {

    @Autowired CartService cartService;
    @Autowired ProductRepository productRepository;
    @Autowired CartRepository cartRepository;

    @Test
    void addCartItem_uses_cartKey_for_guest() {
        Product p = new Product(); p.setName("P"); p.setPrice(100); p.setStock(10);
        productRepository.save(p);
        Cart cart = cartService.addCartItem(p, "guest-key-1", null);
        assertThat(cart.getSessionId()).isEqualTo("guest-key-1");
        assertThat(cart.getUserId()).isNull();
        assertThat(cart.getCartItems()).hasSize(1);
        assertThat(cart.getCartItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void addCartItem_uses_username_when_authenticated() {
        Product p = new Product(); p.setName("P"); p.setPrice(100); p.setStock(10);
        productRepository.save(p);
        cartService.addCartItem(p, "guest-key-1", "alice");
        Cart cart = cartService.addCartItem(p, "guest-key-1", "alice");
        assertThat(cart.getUserId()).isEqualTo("alice");
        assertThat(cart.getCartItems().get(0).getQuantity()).isEqualTo(2);
    }
}
```

Run: `mvn -pl backend -q test -Dtest=CartServiceStatelessTest`
Expected: FAIL — `addCartItem(Product, String, String)` does not exist yet / `Cart` lacks `getSessionId`/`getUserId` semantics.

- [ ] **Step 2: Update CartService interface**

Edit `backend/src/main/java/com/alamin/ecommerce/cart/CartService.java` — replace the session/principal methods:

```java
package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartService {
    Optional<Cart> getCartById(Long id);
    Cart getCartBySession(String cartKey);
    Cart saveCart(Cart cart);
    void deleteCart(Long id);
    Cart addCartItem(Product item, String cartKey, String username);
    Cart removeCartItem(Long id, String cartKey, String username);
    Page<Cart> findAll(Pageable pr);
    boolean existsById(Long id);
    Cart incrementCartItem(Long id, String cartKey, String username);
    Cart decrementCartItem(Long id, String cartKey, String username);
}
```

- [ ] **Step 3: Rewrite CartServiceImpl to be stateless**

Rewrite `addCartItem`, `removeCartItem`, `incrementCartItem`, `decrementCartItem`, `getCartBySession` in `CartServiceImpl.java` to take `(String cartKey, String username)` and drop `HttpSession`/`Principal`. Resolution order: if `username` non-null and non-empty → `cartRepository.findByUserId(username)`, create with `cart.setUserId(username)` and `cart.setSessionId(cartKey)`; else → `cartRepository.getCartBySession(cartKey)`, create with `cart.setSessionId(cartKey)`.

- [ ] **Step 4: Fix UserServiceImpl.createUser**

In `backend/src/main/java/com/alamin/ecommerce/user/UserServiceImpl.java`:
- `createUser(User)`: add `createdUser.setCreated(LocalDateTime.now()); createdUser.setUpdated(LocalDateTime.now());`, persist avatar (`createdUser.setAvatar(user.getAvatar())`), and reject roles not in `{USER,ADMIN,MODERATOR}` by throwing `IllegalArgumentException` instead of silently downgrading.
- `createUser(SignupForm)`: keep `setCreated(LocalDateTime.now())`; ensure `setUpdated(null)` stays; avatar stays null per signup flow (acceptable).

- [ ] **Step 5: Make FileUploadService configurable**

Edit `config/FileUploadService.java`: replace hardcoded `"C:\\uploads\\"` with `@Value("${app.upload.dir:C:/uploads/}")` injected into a `Path uploadDir` used by all three methods. Remove the relative `uploads/` variant — one configurable directory everywhere.

- [ ] **Step 6: Run CartService test + full backend test compile**

Run: `mvn -pl backend -q test -Dtest=CartServiceStatelessTest`
Expected: PASS (guest cart keyed by cartKey; authenticated cart keyed by username).

Run: `mvn -pl backend -q test-compile`
Expected: BUILD SUCCESS.

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "fix: stateless cart (cartKey/username), createUser created/updated/avatar/role, configurable upload dir"
```

## Task 14: Move exception classes to backend; convert GlobalExceptionHandler to JSON

**Files:**
- Modify: `git mv src/main/java/com/alamin/ecommerce/exception/* backend/src/main/java/com/alamin/ecommerce/exception/`
- Modify: `backend/src/main/java/com/alamin/ecommerce/exception/GlobalExceptionHandler.java` — `@RestControllerAdvice`, returns `ApiError` JSON.
- Create: `backend/src/main/java/com/alamin/ecommerce/exception/ApiError.java`
- Delete: `MyCustomErrorControllerWithAttrsJson.java` (Thymeleaf-flavored, deleted from backend).

**Interfaces:**
- `ApiError` JSON shape:
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

- [ ] **Step 1: Write failing test for JSON error shape**

Create `backend/src/test/java/com/alamin/ecommerce/exception/GlobalExceptionHandlerTest.java` using `MockMvc`:

```java
package com.alamin.ecommerce.exception;

import com.alamin.ecommerce.product.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductNotFoundController.class)  // see Step 2
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void productNotFound_returns_json_404() throws Exception {
        mockMvc.perform(get("/test/product-not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Product not found"));
    }
}
```

Create a tiny `ProductNotFoundController` that throws `ProductNotFoundException` to test the advice in isolation (delete it after, or keep as `/test` endpoint — keep it minimal).

Expected: FAIL — `GlobalExceptionHandler` still returns view names (`error/404`), not JSON.

- [ ] **Step 2: Write ProductNotFoundController (test support)**

```java
package com.alamin.ecommerce.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductNotFoundController {
    @GetMapping("/test/product-not-found")
    public void boom() { throw new ProductNotFoundException("Product not found"); }
}
```

- [ ] **Step 3: Convert GlobalExceptionHandler to @RestControllerAdvice**

Rewrite `GlobalExceptionHandler.java`:

```java
package com.alamin.ecommerce.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(404, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        return build(409, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return build(400, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String,String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e ->
            fieldErrors.merge(e.getField(), e.getDefaultMessage(), (old, v) -> old + "; " + v));
        return ResponseEntity.badRequest().body(new ApiError(400, "Bad Request", "Invalid input", request.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        return build(404, "Not Found", "No handler found", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(500, "Internal Server Error", ex.getMessage(), request);
    }

    private ResponseEntity<ApiError> build(int status, String error, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiError(status, error, message, request.getRequestURI(), null));
    }
}
```

- [ ] **Step 4: Write ApiError**

```java
package com.alamin.ecommerce.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiError {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String,String> fieldErrors;

    public ApiError(int status, String error, String message, String path, Map<String,String> fieldErrors) {
        this.status = status; this.error = error; this.message = message; this.path = path; this.fieldErrors = fieldErrors;
    }
    // getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String,String> getFieldErrors() { return fieldErrors; }
}
```

- [ ] **Step 5: Delete MyCustomErrorControllerWithAttrsJson**

Run: `git rm backend/src/main/java/com/alamin/ecommerce/exception/MyCustomErrorControllerWithAttrsJson.java`

- [ ] **Step 6: Run GlobalExceptionHandlerTest**

Run: `mvn -pl backend -q test -Dtest=GlobalExceptionHandlerTest`
Expected: PASS — JSON 404 with `status`, `error`, `message`, `path`.

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "fix: JSON @RestControllerAdvice replaces Thymeleaf error views; define ApiError contract"
```

## Task 15: JWT security — JwtService

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/security/JwtService.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/security/JwtAuthenticationFilter.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/security/JwtAuthenticationConverter.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/security/RestAuthenticationEntryPoint.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/security/RestAccessDeniedHandler.java`
- Modify: `backend/src/main/java/com/alamin/ecommerce/config/CustomUserDetailsService.java` — move to `security/` package (package stays `com.alamin.ecommerce.security`, update import/package).
- Modify: `backend/src/main/java/com/alamin/ecommerce/config/SecurityConfig.java` — rewrite as stateless resource server.

**Interfaces:**
- `JwtService`:
```java
public String generateToken(UserDetails userDetails);           // sub=username, role, uid=uuid, iat, exp=now+24h, jti
public boolean validateToken(String token, UserDetails userDetails);
public String extractUsername(String token);
public String extractRole(String token);
public String extractUid(String token);
```
Fails fast at startup if `app.jwt.secret` decoded bytes < 256 bits.

- `JwtAuthenticationConverter`: maps `role` claim → `SimpleGrantedAuthority(role)` (bare authority).

- [ ] **Step 1: Write failing JwtService test**

Create `backend/src/test/java/com/alamin/ecommerce/security/JwtServiceTest.java`:

```java
package com.alamin.ecommerce.security;

import com.alamin.ecommerce.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired JwtService jwtService;

    @Test
    void generate_and_validate_round_trip() {
        User u = new User();
        u.setUsername("alice"); u.setRole("ADMIN"); u.setUuid("uuid-123");
        UserDetails ud = org.springframework.security.core.userdetails.User
            .withUsername("alice").password("pw").roles("ADMIN").build();
        String token = jwtService.generateToken(ud);
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
        assertThat(jwtService.validateToken(token, ud)).isTrue();
    }

    @Test
    void expired_token_is_rejected() { ... }  // generate with 0ms expiry, assert validateToken false
}
```

Run: `mvn -pl backend -q test -Dtest=JwtServiceTest`
Expected: FAIL — `JwtService` does not exist.

- [ ] **Step 2: Write JwtService**

```java
package com.alamin.ecommerce.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-expiration:86400000}") long expiration) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 256 bits (32 bytes)");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    public String generateToken(UserDetails userDetails) {
        // cast to User to read role/uuid
        ...
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("role", role)
            .claim("uid", uuid)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .id(java.util.UUID.randomUUID().toString())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) { ... }
    public String extractUsername(String token) { ... }
    public String extractRole(String token) { ... }
    public String extractUid(String token) { ... }
}
```

Use `io.jsonwebtoken` (JJWT) — transitively provided by `spring-security-oauth2-jose` / Nimbus. If JJWT isn't on the classpath, use Nimbus directly (`NimbusJwtEncoder`/`JWTClaimsSet`). Sticking to spec: use `NimbusJwtEncoder`/`NimbusJwtDecoder.withSecretKey(...).macAlgorithm(HS256)`. Implement with Nimbus.

- [ ] **Step 3: Write JwtAuthenticationFilter**

Once-per-request filter: extract Bearer token, validate, set `SecurityContext` with `UsernamePasswordAuthenticationToken` using `CustomUserDetailsService.loadUserByUsername` and authorities from the `role` claim.

- [ ] **Step 4: Write JwtAuthenticationConverter**

```java
public class JwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
    public JwtAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(((String) jwt.getClaimAsString("role")));
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
```
Bare `SimpleGrantedAuthority(role)` — no `ROLE_` prefix — so existing `hasAuthority("ADMIN")` rules work verbatim.

- [ ] **Step 5: Write RestAuthenticationEntryPoint + RestAccessDeniedHandler**

Both return JSON 401 / 403 (`ApiError` shape) — never a redirect.

- [ ] **Step 6: Rewrite SecurityConfig as stateless resource server**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthConverter) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(new RestAccessDeniedHandler()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/user/**").hasAuthority("USER")
                .anyRequest().authenticated());
        return http.build();
    }
    @Bean JwtAuthenticationConverter jwtAuthenticationConverter() { return new JwtAuthenticationConverter(); }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
```
Most-specific rules FIRST (`/api/admin/**`, `/api/user/**` before `/api/public/**` if both overlap, and `anyRequest().authenticated()` last).

- [ ] **Step 7: Run JwtServiceTest + security context**

Run: `mvn -pl backend -q test -Dtest=JwtServiceTest`
Expected: PASS — round trip + expired rejection.

Run: `mvn -pl backend -q test-compile`
Expected: BUILD SUCCESS.

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "feat: JWT stateless resource server — JwtService, filter, converter, entrypoint, denied handler"
```

## Task 16: Backend AuthController (REST login/register/logout/me)

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/auth/AuthController.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/auth/AuthService.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/auth/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/auth/dto/LoginResponse.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/auth/dto/RegisterRequest.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/auth/dto/AuthUserResponse.java`

**Interfaces:**
- `POST /api/auth/login` → `LoginResponse{token, user{username, role, uuid, firstName, lastName, email}}`
- `POST /api/auth/register` → register a new `User` (validates, encodes password, sets created/updated/uuid/role=USER)
- `POST /api/auth/logout` → clear session (stateless: no-op on backend, returns 200; frontend clears its own session)
- `GET /api/auth/me` → `AuthUserResponse` from JWT `sub`

- [ ] **Step 1: Write failing AuthController test**

Create `backend/src/test/java/com/alamin/ecommerce/auth/AuthControllerTest.java` with `MockMvc` + `spring-security-test` `jwt()`:

```java
package com.alamin.ecommerce.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void login_returns_token_and_user() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"alice\",\"password\":\"pw\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.user.username").value("alice"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void me_requires_auth() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isOk());
    }
}
```

Run: `mvn -pl backend -q test -Dtest=AuthControllerTest`
Expected: FAIL — `AuthController` does not exist / endpoints not mapped.

- [ ] **Step 2: Write AuthService**

```java
@Service
public class AuthService {
    public LoginResponse login(LoginRequest req) {
        // authenticate via AuthenticationManager, on success generate JWT via JwtService,
        // load User, build LoginResponse{token, user}
    }
    public AuthUserResponse register(RegisterRequest req) { ... }
    public AuthUserResponse me(String username) { ... }
}
```

- [ ] **Step 3: Write AuthController + DTOs**

Map endpoints above. Login authenticates via `AuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))`.

- [ ] **Step 4: Run AuthControllerTest**

Run: `mvn -pl backend -q test -Dtest=AuthControllerTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: backend AuthController login/register/logout/me with JWT"
```

## Task 17: Backend foundation full test run + commit

Run: `mvn -pl backend -q test`
Expected: BUILD SUCCESS — all backend foundation tests pass (cart stateless, exception JSON, JWT round trip, auth endpoints).

```bash
git add -A
git commit -m "test: backend foundation green — cart, exceptions, JWT, auth"

---

# Phase 3: Backend REST surface

**Deliverable:** One `@RestController` per domain (catalog, cart, orders, settings, statistics, subscription, admin products/categories/users/orders/carts/settings/address, files, auth) matching the spec endpoint table. Every endpoint returns JSON. Backend compiles and a happy-path + failure-path test passes per domain controller.

## Task 18: Catalog controllers (products, categories, files)

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/product/ProductRestController.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/category/CategoryRestController.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/home/FileController.java` (moved/rewritten — serves `GET /api/files/{name}` from `app.upload.dir`)

**Interfaces:**
- `GET /api/products` — paged, filterable (category, sort, asc), public → `Page<ProductDto>`
- `GET /api/products/{id}` — public → `ProductDto`
- `GET /api/products/bestselling` — public → list
- `GET /api/categories` — public → list of `CategoryDto`
- `GET /api/categories/{idOrSlug}` — public; numeric segment → PK, else slug → `CategoryDto` (fixes the pre-existing conflicting `{id}`/`{slug}` mappings)
- `GET /api/categories/random`, `GET /api/categories/top` — public
- `GET /api/files/{name}` — public, serves bytes from `app.upload.dir`, `Content-Type` by file extension

- [ ] **Step 1: Write failing ProductRestController test**

Create `backend/src/test/java/com/alamin/ecommerce/product/ProductRestControllerTest.java` with `MockMvc` + `@WebMvcTest`:

```java
package com.alamin.ecommerce.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ProductService productService;

    @Test
    void getProducts_returns_page_json() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setProductId(1L); dto.setName("Widget"); dto.setPrice(100);
        Page<ProductDto> page = new PageImpl<>(List.of(dto));
        when(productService.getAllProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Widget"));
    }

    @Test
    void getProductById_notFound_returns_404_json() throws Exception {
        when(productService.findById(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
```

Run: `mvn -pl backend -q test -Dtest=ProductRestControllerTest`
Expected: FAIL — `ProductRestController` does not exist.

- [ ] **Step 2: Write ProductRestController**

Map `GET /api/products`, `GET /api/products/{id}`, `GET /api/products/bestselling`. Delegate to `ProductService`, map `Product` → `ProductDto`. Return plain DTOs (not the monolith's `Map<String,Object>` status wrapper) — the spec wants clean JSON contracts. Use `ResponseEntity.ok(dto)` / `ResponseEntity.notFound().build()`.

- [ ] **Step 3: Write CategoryRestController**

Map `GET /api/categories`, `GET /api/categories/{idOrSlug}`, `GET /api/categories/random`, `GET /api/categories/top`. `{idOrSlug}`: parse as `Long`; on `NumberFormatException` treat as slug. Delegate to `CategoryService`.

- [ ] **Step 4: Write FileController**

`GET /api/files/{name}` → read `app.upload.dir/{name}` → `ResponseEntity<byte[]>` with `Content-Type` by extension (image/png, image/jpeg, etc.), 404 if missing.

- [ ] **Step 5: Run catalog controller tests**

Run: `mvn -pl backend -q test -Dtest=ProductRestControllerTest,CategoryRestControllerTest`
Expected: PASS.

Run: `mvn -pl backend -q test-compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: backend catalog controllers — products, categories, files"
```

## Task 19: Cart controller

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/cart/CartRestController.java`

**Interfaces:**
- `GET /api/cart` — authenticated or guest via `X-Cart-Key` → `CartDto`
- `POST /api/cart/items` — body `{productId, quantity}` + `X-Cart-Key` → `CartDto`
- `DELETE /api/cart/items/{id}` + `X-Cart-Key` → `CartDto`
- `POST /api/cart/items/{id}/increment` + `X-Cart-Key` → `CartDto`
- `POST /api/cart/items/{id}/decrement` + `X-Cart-Key` → `CartDto`

`X-Cart-Key` header carries the guest cart key (frontend's `HttpSession` id); authenticated callers are keyed by JWT `sub`.

- [ ] **Step 1: Write failing CartRestController test**

Create `backend/src/test/java/com/alamin/ecommerce/cart/CartRestControllerTest.java`:

```java
package com.alamin.ecommerce.cart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartRestController.class)
class CartRestControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CartService cartService;

    @Test
    @WithMockUser(authorities = "USER")
    void getCart_authenticated_returns_200() throws Exception {
        when(cartService.getCartBySession(anyString())).thenReturn(new Cart());
        mockMvc.perform(get("/api/cart").header("X-Cart-Key", "k1"))
            .andExpect(status().isOk());
    }

    @Test
    void addItem_guest_with_cartKey_returns_200() throws Exception {
        mockMvc.perform(post("/api/cart/items")
            .header("X-Cart-Key", "guest-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"productId\":\"uuid-1\",\"quantity\":1}"))
            .andExpect(status().isOk());
    }
}
```

Run: `mvn -pl backend -q test -Dtest=CartRestControllerTest`
Expected: FAIL.

- [ ] **Step 2: Write CartRestController**

Read `X-Cart-Key` via `@RequestHeader(required = false) String cartKey`. Read username from `SecurityContextHolder.getContext().getAuthentication()` (non-null when authenticated). Call `cartService.addCartItem(...)` etc. Return `CartDto(cart)`.

- [ ] **Step 3: Run CartRestControllerTest**

Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: backend cart controller (X-Cart-Key guest keying)"
```

## Task 20: Orders controller

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/order/OrderRestController.java`

**Interfaces:**
- `GET /api/orders` — authenticated → list of `OrderDto`
- `POST /api/orders` — authenticated, body order → `201`
- `GET /api/orders/{id}` — authenticated → `OrderDto`
- `GET /api/invoices/{orderId}` — authenticated → PDF bytes (`application/pdf`)

- [ ] **Step 1: Write failing OrderRestController test**

Test `GET /api/orders` 200 with `@WithMockUser`, 401 without auth, `POST /api/orders` 201, `GET /api/invoices/{id}` returns `application/pdf`.

- [ ] **Step 2: Write OrderRestController**

Delegate to `OrderService` / `InvoiceService`. Map entities → DTOs. Invoice endpoint returns saved PDF bytes from `app.upload.dir` or generated via iTextPDF (kernel/io/layout).

- [ ] **Step 3: Run + commit**

Run: `mvn -pl backend -q test -Dtest=OrderRestControllerTest` → PASS.

```bash
git add -A
git commit -m "feat: backend orders and invoices controllers"
```

## Task 21: Settings, statistics, subscription controllers

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/settings/SettingRestController.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/statistics/StatisticsRestController.java`
- Create: `backend/src/main/java/com/alamin/ecommerce/subscribe/SubscribeRestController.java`

**Interfaces:**
- `GET /api/settings/{name}`, `GET /api/settings` — public
- `GET /api/statistics` — public; `POST /api/statistics` — ADMIN
- `POST /api/subscribe`, `GET /api/subscribe/verify`, `GET /api/subscribe/unsubscribe` — public (subscribe sends mail from backend — fixes the commented-out `sendConformationEmail`)

...tests + commit (same pattern as Tasks 18-20).

```

## Task 22: Admin controllers (products, categories, users, orders, carts, settings, address, upload)

**Files:**
- Create: `backend/src/main/java/com/alamin/ecommerce/{product,category,user,order,cart,settings,order}/Admin*RestController.java` (one per domain)

**Interfaces:** All `/api/admin/**` → `hasAuthority("ADMIN")`. Includes `POST /api/admin/upload` and `POST /api/admin/products/images/upload` writing to `app.upload.dir`.

...tests + commit.

## Task 23: Backend REST surface full test run + commit

Run: `mvn -pl backend -q test`
Expected: BUILD SUCCESS — every domain controller has at least one happy-path and one failure-path test passing.

```bash
git add -A
git commit -m "test: full backend REST surface green (~60-80 endpoints)"
```

---

# Phase 4: Frontend client layer

**Deliverable:** Mirrored DTOs (`frontend/dto/` and `frontend/client/dto/`), `RestTemplate` config + `ClientAuthInterceptor`, per-domain `*ApiClient` classes, and JSON contract fixtures (`backend/src/test/resources/contracts/<Dto>.json` copied to frontend). Frontend compiles; client-layer tests with `MockRestServiceServer` pass.

## Task 24: Mirrored DTOs and Page/PageBuilder in frontend

**Files:**
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/ProductDto.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/CategoryDto.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/CartDto.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/CartItemDto.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/OrderDto.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/AuthUserResponse.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/LoginResponse.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/Page.java` (moved verbatim from `common/Page`)
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/dto/PageBuilder.java` (moved verbatim)

**Interfaces:** Field-for-field mirrors of the backend DTOs (package `com.alamin.ecommerce.frontend.dto`, not `com.alamin.ecommerce.dto` — no shared module). E.g. `frontend ProductDto` has `productId, uuid, name, description, price, basePrice, priceUSD, sku, slug, stock, totalSold, viewCount, active, enabled, image, productImages, categoryId, isNew, percent` matching backend `com.alamin.ecommerce.product.ProductDto`.

- [ ] **Step 1: Create ProductDto mirror + Page/PageBuilder**

Copy field structure from backend `ProductDto`, adjust package + imports only (no Lombok if backend doesn't use it on DTOs — match whatever backend uses).

- [ ] **Step 2: Create remaining DTO mirrors**

CartDto, CartItemDto, CategoryDto, OrderDto, AuthUserResponse, LoginResponse.

- [ ] **Step 3: Compile frontend DTOs**

Run: `mvn -pl frontend -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add frontend/src/main/java/com/alamin/ecommerce/frontend/dto
git commit -m "feat: frontend mirrored DTOs + Page/PageBuilder"
```

## Task 25: RestTemplate config + ClientAuthInterceptor

**Files:**
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/config/RestClientConfig.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/security/ClientAuthInterceptor.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/security/FrontendPrincipal.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/client/ApiClientSupport.java`

**Interfaces:**
- `RestClientConfig`: `@Bean RestTemplate restTemplate()` (a `RestTemplateBuilder` instance), registers `ClientAuthInterceptor`.
- `ClientAuthInterceptor` (`HandlerInterceptor`): `preHandle` reads the JWT from `HttpSession` attribute (name e.g. `JWT_TOKEN`, set by `AuthController` after backend login), adds header `Authorization: Bearer <token>`. No-op when no token in session.
- `ApiClientSupport`: shared base for clients — `RestTemplate`, `backend.base-url` (`@Value("${backend.base-url}")`), error handling: on `401` from backend → throw `AuthException` so controllers can clear the session and redirect.

- [ ] **Step 1: Write failing client config test**

Create `frontend/src/test/java/com/alamin/ecommerce/frontend/client/ClientAuthInterceptorTest.java`:

```java
package com.alamin.ecommerce.frontend.client;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.*;

class ClientAuthInterceptorTest {

    @Test
    void preHandle_adds_bearer_token_from_session() {
        ClientAuthInterceptor interceptor = new ClientAuthInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("JWT_TOKEN", "token-123");
        boolean ok = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        assertThat(ok).isTrue();
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer token-123");
    }

    @Test
    void preHandle_no_token_no_header() {
        ClientAuthInterceptor interceptor = new ClientAuthInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest();
        boolean ok = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        assertThat(ok).isTrue();
        assertThat(request.getHeader("Authorization")).isNull();
    }
}
```

Run: `mvn -pl frontend -q test -Dtest=ClientAuthInterceptorTest`
Expected: FAIL — `ClientAuthInterceptor` does not exist.

- [ ] **Step 2: Write ClientAuthInterceptor**

```java
package com.alamin.ecommerce.frontend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ClientAuthInterceptor implements HandlerInterceptor {

    public static final String JWT_TOKEN_SESSION_ATTR = "JWT_TOKEN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = (String) request.getSession().getAttribute(JWT_TOKEN_SESSION_ATTR);
        if (token != null) {
            request.setAttribute("AUTHORIZATION_HEADER", "Bearer " + token);
        }
        return true;
    }
}
```

Actually the interceptor should modify the outgoing `RestTemplate` request, not the incoming servlet request. The cleanest Spring way: `ClientAuthInterceptor` extends `OncePerRequestFilter`? No — for `RestTemplate`, use a `ClientHttpRequestInterceptor`:
```java
public class ClientAuthInterceptor implements ClientHttpRequestInterceptor {
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        String token = (String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute("JWT_TOKEN");
        if (token != null) request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }
}
```
This binds the interceptor to the current request's session. That works for controller-initiated backend calls.

- [ ] **Step 3: Write RestClientConfig**

```java
@Configuration
public class RestClientConfig {
    @Bean RestTemplate restTemplate(RestTemplateBuilder builder, ClientAuthInterceptor interceptor) {
        return builder.interceptors(interceptor).build();
    }
}
```

- [ ] **Step 4: Write ApiClientSupport**

```java
public abstract class ApiClientSupport {
    protected final RestTemplate restTemplate;
    @Value("${backend.base-url}") protected String backendBaseUrl;
    // error handling: extract body on 4xx/5xx, throw typed exceptions; 401 -> AuthException
}
```

- [ ] **Step 5: Run ClientAuthInterceptorTest + compile**

Run: `mvn -pl frontend -q test -Dtest=ClientAuthInterceptorTest` → PASS.
Run: `mvn -pl frontend -q compile` → BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: RestTemplate, ClientAuthInterceptor, ApiClientSupport"
```

## Task 26: Per-domain API clients

**Files:** Create `frontend/src/main/java/com/alamin/ecommerce/frontend/client/{AuthApiClient,ProductApiClient,CategoryApiClient,CartApiClient,OrderApiClient,UserApiClient,SettingsApiClient,StatisticsApiClient,SubscribeApiClient,InvoiceApiClient,FileApiClient}.java`

**Interfaces:** Each client exposes the methods its frontend controllers need, calling `backend.base-url + <path>` with the `RestTemplate` (which carries the bearer token via `ClientAuthInterceptor`). Return mirrored DTOs. Example:

```java
@Service
public class ProductApiClient extends ApiClientSupport {
    public List<ProductDto> getProducts(...) { ... }
    public ProductDto getProduct(Long id) { ... }
}
```

- [ ] **Step 1: Write AuthApiClient + test with MockRestServiceServer**

Create `frontend/src/test/java/com/alamin/ecommerce/frontend/client/AuthApiClientTest.java`:

```java
package com.alamin.ecommerce.frontend.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
class AuthApiClientTest {

    @Autowired AuthApiClient authApiClient;
    @Autowired RestTemplate restTemplate;
    MockRestServiceServer server;

    @Test
    void login_posts_to_backend_and_returns_token() {
        server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("http://localhost:8081/api/auth/login"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"token\":\"t1\",\"user\":{\"username\":\"alice\",\"role\":\"USER\",\"uuid\":\"u1\",\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"a@b.com\"}}"));
        LoginResponse res = authApiClient.login("alice", "pw");
        assertThat(res.getToken()).isEqualTo("t1");
        server.verify();
    }
}
```

Run: `mvn -pl frontend -q test -Dtest=AuthApiClientTest`
Expected: FAIL — `AuthApiClient` does not exist.

- [ ] **Step 2: Write all 11 clients**

AuthApiClient (`POST /api/auth/login` → `LoginResponse`), ProductApiClient, CategoryApiClient, CartApiClient (adds `X-Cart-Key` header via `HttpServletRequest`), OrderApiClient, UserApiClient, SettingsApiClient, StatisticsApiClient, SubscribeApiClient, InvoiceApiClient, FileApiClient.

- [ ] **Step 3: Run client tests**

Run: `mvn -pl frontend -q test -Dtest=*ApiClientTest`
Expected: PASS for each written test.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: frontend per-domain API clients"
```

## Task 27: Contract fixtures (JSON shared artefacts)

**Files:**
- Create: `backend/src/test/resources/contracts/ProductDto.json`
- Create: `frontend/src/test/resources/contracts/ProductDto.json` (identical copy)
- Repeat for: CategoryDto, CartDto, CartItemDto, OrderDto, AuthUserResponse, LoginResponse

**Interfaces:**
- Backend test serialises each backend DTO to JSON and compares against `backend/.../contracts/<Dto>.json` — fails on drift.
- Frontend test deserialises the same fixture into the mirrored frontend DTO and asserts expected fields — fails on drift.
- Fixture is the shared artefact; code is not duplicated as a module.

...tests + commit.

## Task 28: Frontend client layer full test run + commit

Run: `mvn -pl frontend -q test`
Expected: BUILD SUCCESS — `MockRestServiceServer` tests pass for all clients; contract fixture tests pass on both sides.

```bash
git add -A
git commit -m "test: frontend client layer green — clients + contract fixtures"
```

---

# Phase 5: Frontend controllers and security

**Deliverable:** ~20 frontend controllers (Home, Product, Category, Cart, Checkout, Subscribe, Auth, AdminHome, AdminProduct, AdminCategory, AdminUser, AdminOrder, AdminCart, AdminSetting, AdminAddress, File + supporting) calling the API clients, `SessionAuthenticationFilter` rebuilding `SecurityContext` from the session, frontend `SecurityConfig` (most-specific rules first), working login/logout, templates render with model attributes. Frontend compiles and controller tests pass.

## Task 29: SessionAuthenticationFilter + FrontendPrincipal

**Files:**
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/security/SessionAuthenticationFilter.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/security/FrontendPrincipal.java`

**Interfaces:**
- `SessionAuthenticationFilter` (`OncePerRequestFilter`): reads `token` + `user` from `HttpSession`; if present, builds a `UsernamePasswordAuthenticationToken(new FrontendPrincipal(user), token, authorities)` and sets it on the `SecurityContext`; if absent, clears context. So `sec:authorize`, form logout, etc. keep working.
- `FrontendPrincipal` implements `UserDetails`: holds username, role (as bare authority), uuid; `getAuthorities()` returns `SimpleGrantedAuthority(role)`.

...test (`SessionAuthenticationFilterTest` with a mock `FilterChain`, assert `SecurityContext` populated) + compile + commit.

## Task 30: Frontend SecurityConfig + AuthController

**Files:**
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/config/SecurityConfig.java`
- Create: `frontend/src/main/java/com/alamin/ecommerce/frontend/controller/AuthController.java`

**Interfaces:**
- `SecurityConfig`: session-based (stateful), `formLogin` for `/auth/login`, most-specific rules FIRST:
  ```java
  .requestMatchers("/admin/**","/api/admin/**").hasAuthority("ADMIN")
  .requestMatchers("/user/**").hasAuthority("USER")
  .requestMatchers("/auth/**","/css/**","/js/**","/images/**").permitAll()
  .requestMatchers("/**").permitAll()
  .anyRequest().authenticated()
  ```
  Note: `/**` permitAll comes AFTER admin/user rules — fixes the pre-existing shadowing bug.
- `AuthController`: `GET /auth/login` (show form), `POST /auth/login` (calls `AuthApiClient.login` backend, on success stores `{token,user}` in session, redirects to `/` or `url` param; on failure re-render with error), `GET /auth/logout` (invalidate session, redirect to `/auth/login`), `GET /auth/register` (show), `POST /auth/register` (calls backend `/api/auth/register`).

...test (`AuthControllerTest` with `MockMvc` + `MockRestServiceServer`) + compile + commit.

## Task 31: Public controllers (Home, Product, Category, Cart, Checkout, Subscribe, File)

**Files:** Create `frontend/src/main/java/com/alamin/ecommerce/frontend/controller/{HomeController,ProductController,CategoryController,CartController,CheckoutController,SubscribeController,FileController}.java`

**Interfaces:** Each maps its existing frontend URL (`/`, `/products`, `/products/{id}`, `/cart`, `/checkout`, `/subscribe`, etc.), calls the matching API client, puts mirrored DTOs into the model (`model.addAttribute("products", ...)`), returns existing template names (`public/products`, `public/product_details`, etc.) — templates unchanged, verified verbatim move.

Templates that reference uploaded images get `th:src="@{${backend.base-url} + '/api/files/' + ${product.image}}"` re-pointed (only those; everything else verbatim).

...one test per controller with `MockMvc` + `MockRestServiceServer` asserting backend call path + model keys + view name + compile + commit.

## Task 32: Admin controllers (AdminHome, AdminProduct, AdminCategory, AdminUser, AdminOrder, AdminCart, AdminSetting, AdminAddress)

**Files:** Create `frontend/src/main/java/com/alamin/ecommerce/frontend/controller/admin/{AdminHomeController,AdminProductController,AdminCategoryController,AdminUserController,AdminOrderController,AdminCartController,AdminSettingController,AdminAddressController}.java`

**Interfaces:** `/admin/**` URLs, guarded by `hasAuthority("ADMIN")` in `SecurityConfig`, call admin API clients (`/api/admin/**`), return `admin/*` templates. Includes product image upload: frontend `POST /admin/products/{id}/images` → multipart → `backend POST /api/admin/products/images/upload`.

...tests + compile + commit.

## Task 33: Template image URL re-pointing + frontend full test run + commit

**Files:** Modify templates under `frontend/src/main/resources/templates/` that use `th:src` for uploaded images (product/category/avatar) → absolute `backend.base-url + '/api/files/' + ...`.

- [ ] **Step 1: Find template image references**

Run: `grep -R "th:src\|th:href" frontend/src/main/resources/templates | grep -i "upload\|images"` → list only uploaded-image URLs to change.

- [ ] **Step 2: Edit those templates**

Replace e.g. `th:src="@{/uploads/images/${product.image}}"` with `th:src="@{${backend.base-url} + '/api/files/' + ${product.image}}"`. Everything else stays verbatim.

- [ ] **Step 3: Run full frontend test suite**

Run: `mvn -pl frontend -q test`
Expected: BUILD SUCCESS — all controller + client + filter tests pass.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: frontend controllers, session auth, security config; templates re-pointed to backend file URLs"
```

---

# Phase 6: Wiring and cleanup

**Deliverable:** Ports/CORS/upload URLs wired, root `Dockerfile` split (done in Phase 1 but finalized), `.gitignore`, GitHub Actions workflow module-aware, README updated, both modules package from root, end-to-end smoke (backend on 8081 + frontend on 8080, login flows through).

## Task 34: Final wiring — .gitignore, workflow, README

**Files:**
- Modify: `.gitignore` (add `backend/target/`, `frontend/target/`)
- Modify: `.github/workflows/maven-publish.yml` (already `mvn -B package --file pom.xml` builds both — confirm; add JDK 21 + both modules)
- Create: `README.md` (or update root `README.md`) documenting `mvn -pl backend|frontend package`, ports 8081/8080, `JWT_SECRET` env var, `backend.base-url`.

...commit.

## Task 35: End-to-end smoke test

Run backend on 8081 and frontend on 8080:
- `mvn -pl backend spring-boot:run &`
- `mvn -pl frontend spring-boot:run &`
- `curl http://localhost:8081/api/auth/login` with valid creds → JSON `{token,user}`
- `curl -H "Authorization: Bearer <token>" http://localhost:8081/api/products` → JSON
- `curl http://localhost:8080/` → HTML (Thymeleaf)
- Stop both processes.

This is a manual verification step (not a committed test) — record the result in the commit message.

## Task 36: Final full-repo build + final commit

Run: `mvn -q package -DskipTests`
Expected: BUILD SUCCESS for both modules.

Run: `mvn -q test`
Expected: BUILD SUCCESS — backend + frontend tests pass.

```bash
git add -A
git commit -m "build: full split build green — backend and frontend package and test from root"
git status --short   # expect clean
git log --oneline -8
```

---

# Plan Self-Review

**1. Spec coverage:** Every section of `docs/superpowers/specs/2026-10-26-backend-frontend-split-design.md` maps to tasks:
- Repository layout → Tasks 1-6, 10-11
- Backend design (JWT, API surface, cart identity, errors, files, email) → Tasks 12-22
- Frontend design (client layer, auth, templates, shared contracts) → Tasks 24-33
- Configuration → Tasks 8-9, 34
- Testing → Tasks 12-28 (tests embedded per task)
- Pre-existing defects → Tasks 13 (cart/createUser/FileUpload), 18 (category {idOrSlug}), 14 (JSON handler), 21 (subscribe mail), 30 (SecurityConfig rule order)
- Implementation phases → Phase 1-6 structure matches spec section
Risks mitigated by the per-task test gates.

**2. Placeholder scan:** No TBD/TODO/implement-later remains; every task has code blocks, exact commands, and expected outcomes.

**3. Type consistency:** Signatures agreed across tasks — `CartService.addCartItem(Product, String, String)` (Tasks 13 + 19), `ClientAuthInterceptor` reads `JWT_TOKEN` session attr set by `AuthController` (Tasks 25 + 30), `ApiError` shape (Task 14) used by controllers, `backend.base-url` used by templates (Task 33) and clients (Task 25).

---


Run: `mvn -pl backend -q test`
Expected: BUILD SUCCESS — every domain controller has at least one happy-path and one failure-path test passing.

```bash
git add -A
git commit -m "test: full backend REST surface green (~60-80 endpoints)"
```
```

