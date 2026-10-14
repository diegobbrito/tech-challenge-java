# TechChallenge API

REST API built with Java and Spring Boot for user management and secure authentication. The application uses Spring Security and JWT to authenticate requests and control access to resources based on user roles.

## Technologies

- **Java 21**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT** with the HS256 algorithm
- **Maven**
- **BCrypt** for password hashing

## Features

- User registration and management.
- Authentication using username and password.
- JWT access token generation.
- Refresh tokens persisted in the database.
- Token renewal with refresh token rotation.
- Refresh token revocation on logout.
- Role-based access control (`USER` and `ADMIN`).
- User deletion restricted to administrators.
- User role changes restricted to administrators.
- Endpoint protection with Spring Security.

## Prerequisites

Before running the project, make sure you have installed:

- Java 21 or a version compatible with the project configuration.
- Maven, unless you use the Maven Wrapper.
- PostgreSQL.

## Environment Setup

### 1. Clone the repository

```bash
git clone <REPOSITORY_URL>
cd techchallenge
```

### 2. Configure the database

Create a PostgreSQL database:

```sql
CREATE DATABASE techchallenge;
```

Configure the database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/techchallenge}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate

jwt.secret=${JWT_SECRET}
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000
```

**Important:** Adjust these properties to match your current application configuration. The `jwt.secret` property must contain a Base64-encoded key representing at least 32 random bytes for HS256.

Generate a key using:

```bash
openssl rand -base64 32
```

Set the environment variables before running the application:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/techchallenge"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
export JWT_SECRET="YOUR_BASE64_SECRET"
```

Do not commit credentials or secrets to Git.

### 3. Prepare the database schema

The project uses PostgreSQL. If tables are created through `init.sql`, make sure the script uses PostgreSQL-compatible syntax and has been executed against the correct database.

The `users` table stores user information, including the identifier, name, email, username, hashed password, address, timestamps, and role.

The `refresh_tokens` table stores refresh token hashes, associated users, expiration timestamps, and revocation status.

If the project uses Flyway or Liquibase migrations, prefer that mechanism to version database schema changes.

### 4. Run the application

Using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or, if Maven is installed:

```bash
mvn spring-boot:run
```

Run the tests with:

```bash
./mvnw test
```

## Authentication and Authorization

The application uses two types of tokens:

| Token | Purpose | Default lifetime |
|---|---|---|
| Access token | Authorizes requests to protected endpoints | 15 minutes |
| Refresh token | Requests new tokens without requiring the password again | 7 days |

The access token is a JWT signed using HS256. The refresh token is a random value stored by the client, while its hash is persisted in the database.

To access protected endpoints, include the access token in the HTTP header:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

### User Roles

- `USER`: Can access resources permitted to authenticated users.
- `ADMIN`: Has administrative permissions, including deleting users and changing user roles.

Public registration must assign the `USER` role on the server side. Clients must not be able to grant themselves administrator privileges by submitting `"userType": "ADMIN"`.

## Authentication Endpoints

The following examples assume the endpoints are `/auth/login`, `/auth/refresh`, and `/auth/logout`.

### Login

**POST** `/auth/login`

Authenticates a user and returns an access token and a refresh token.

Request:

```json
{
  "userLogin": "username",
  "password": "your_password"
}
```

Expected response:

```json
{
  "accessToken": "<ACCESS_TOKEN>",
  "refreshToken": "<REFRESH_TOKEN>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Token Refresh

**POST** `/auth/refresh`

Uses the current refresh token to issue a new access token and a new refresh token.

Request:

```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```

After renewal, the previous refresh token should be invalidated and replaced with the new one.

### Logout

**POST** `/auth/logout`

Revokes the refresh token provided in the request.

Request:

```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```

Logout prevents further token renewals using that refresh token. An already-issued JWT access token remains valid until it expires unless an additional access token revocation mechanism is implemented.

## User Endpoints

The following endpoints represent the suggested API structure. Check the controller mappings before using them.

| Method | Endpoint | Permission |
|---|---|---|
| `POST` | `/users` | Public |
| `GET` | `/users` | Authenticated |
| `GET` | `/users/{id}` | Authenticated, subject to access rules |
| `PUT` | `/users/{id}` | Subject to update authorization rules |
| `PATCH` | `/users/{id}/type` | `ADMIN` |
| `DELETE` | `/users/{id}` | `ADMIN` |

Protected endpoints require the `Authorization: Bearer <ACCESS_TOKEN>` header.

Role changes should use a dedicated endpoint rather than allowing the regular user update DTO to modify `userType`.

## Security

The application follows these security practices:

- Passwords are stored using BCrypt hashing, never in plaintext.
- Access tokens are signed using a strong secret key.
- Refresh tokens are randomly generated, with only their hashes stored in the database.
- Refresh tokens support expiration, rotation, and revocation.
- Access control is based on Spring Security authorities.
- Administrative operations are restricted to administrators.
- JPA entities are separated from response DTOs to avoid exposing user passwords.

For production deployments, use HTTPS, store secrets in an appropriate secrets manager, validate CORS settings, and implement authorization and concurrent token refresh tests.

## Project Structure

The project may be organized into the following packages:

```text
src/
└── main/
    ├── java/br/com/techchallenge/techchallenge/
    │   ├── controllers/
    │   ├── dtos/
    │   ├── entities/
    │   ├── mappers/
    │   ├── repositories/
    │   ├── services/
    │   └── config/
    └── resources/
        ├── application.properties
        └── init.sql
```

The actual structure may vary depending on the packages and files present in the repository.

## Recommended Tests

Before deploying the application, validate the following scenarios:

- Login with valid and invalid credentials.
- Access to protected endpoints without a token or with an expired token.
- Refresh using valid, expired, or revoked refresh tokens.
- Prevention of refresh token reuse after rotation.
- Prevention of user deletion by non-administrators.
- Prevention of users changing their own role to `ADMIN`.
- Prevention of administrator registration through the public registration endpoint.
- No passwords or secrets exposed in API responses.