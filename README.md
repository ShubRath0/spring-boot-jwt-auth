# Auth Service

A production-grade authentication microservice built with Spring Boot 4, Spring Security 6 and JWT (JSON Web Tokens).

Key features
- Stateless authentication using JWTs
- Password hashing with BCrypt
- Role-based access control (RBAC)
- Rate limiting for sensitive endpoints (registration / login) via Bucket4j
- Postgres persistence via Spring Data JPA
- OpenAPI UI via springdoc

Tech stack
- Java 25
- Spring Boot 4.0.x
- Spring Security 6
- PostgreSQL
- Maven (wrapper provided)

Repository layout
- `src/main/java` — application source
  - `com.brandon.auth_service.api.controllers.UserController` — API endpoints for registration, login and token test
  - `com.brandon.auth_service.security.JwtService` — JWT generation and validation
  - `src/main/resources/application.properties` — runtime configuration

Prerequisites
- Java 25 (JDK 25)
- Maven (or use the provided wrapper)
- PostgreSQL (or an accessible Postgres instance)

Configuration
The project reads configuration from `src/main/resources/application.properties` and supports overriding via environment variables.
Important properties:
- `spring.datasource.url` — JDBC URL for Postgres
- `spring.datasource.username` — DB user
- `spring.datasource.password` — DB password
- `app.security.jwt.secret` — JWT HMAC secret (can be set via `JWT_SECRET` env var)

A minimal example for local development (export these in your shell or use a `.env` mechanism):

```bash
# Linux / macOS
export JWT_SECRET="super-secret-local-key-please-change"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/authdb"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="postgres"
```

Run (Windows)
```powershell
# Use the included Maven wrapper on Windows
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

Run (Unix)
```bash
./mvnw clean package
./mvnw spring-boot:run
```

Build a runnable JAR
```bash
./mvnw clean package
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

API Endpoints
Base path: `POST /api/v1/users`

1) Create (registration)
- POST `/api/v1/users/create`
- Request JSON:
```json
{
  "email": "alice@example.com",
  "password": "password123",
  "firstName": "Alice",
  "lastName": "Doe"
}
```
- Success: `201 Created` — returns `RegistrationResponse` containing `authorization` (type + jwt) and `user` profile.

2) Login
- POST `/api/v1/users/login`
- Request JSON:
```json
{
  "email": "alice@example.com",
  "password": "password123"
}
```
- Success: `200 OK` — returns `LoginResponse` with `authorization` (type + jwt) and `user`.

3) Test token (protected)
- POST `/api/v1/users/test`
- Requires Authorization header: `Authorization: Bearer <jwt>`
- Success: `200 OK` — confirms token validity.

Examples (curl)
```bash
# Register
curl -X POST http://localhost:8080/api/v1/users/create \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password123","firstName":"Alice","lastName":"Doe"}'

# Login
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password123"}'

# Test token
curl -X POST http://localhost:8080/api/v1/users/test \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

JWT details
- Secret property: `app.security.jwt.secret` (can be set with `JWT_SECRET` env var)
- Tokens are HMAC-signed using the configured secret
- Token expiration in code is currently fixed to 24 hours (see `JwtService`)

Testing
- Unit tests live under `src/test/java`. Run:
```bash
./mvnw test
```

Notes & next steps
- Rotate `app.security.jwt.secret` in deployed environments and keep it out of source control
- Consider moving secrets into a vault or secret manager for production
- Add refresh tokens if long sessions are required

Contributing
- Fork, create a feature branch, test locally and open a pull request.

License
- Add a LICENSE file if you intend to publish this code.

Files of interest
- `src/main/java/com/brandon/auth_service/api/controllers/UserController.java`
- `src/main/java/com/brandon/auth_service/security/JwtService.java`
- `src/main/resources/application.properties`

---
Generated README for local development and quick onboarding.
