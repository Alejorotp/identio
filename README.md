# Identio Security Backend

The Identio Security Backend is a robust, modular monolith designed to act as the "User Information Core" for the Identio physical security and facial recognition system. It handles core identity management, role-based access control, location tracking, and asynchronous audit logging.

Built with **Spring Boot 4.x**, **Java 21**, and strictly adhering to **Clean Architecture** principles, this service is designed for extremely high throughput, maintainability, and seamless integration with external AI inference engines.

## Architecture

The codebase follows a rigorous Clean Architecture (Onion Architecture) to ensure maximum decoupling between business rules and external frameworks.

```text
com.identio.mvp
├── application/       # Application Business Rules (Use Cases, DTOs)
├── domain/            # Enterprise Business Rules (Entities, Enums, Interfaces)
├── infrastructure/    # Frameworks & Drivers (DB Connections, Security, Spring Configs)
└── interfaces/        # Interface Adapters (REST Controllers, gRPC Endpoints)
```

### Core Principles
- **Dependency Inversion**: The core `domain` has zero dependencies on Spring Framework or external libraries. Interfaces (contracts) are defined in the domain and implemented in the `infrastructure`.
- **Protocol Agnosticism**: The `application` layer (Use Cases) doesn't care if a request comes from HTTP, gRPC, or RabbitMQ. All inputs converge on standard Use Cases.
- **Strict Validation**: All input boundaries are validated using Jakarta Validation constraints.

## Tech Stack
- **Framework**: Spring Boot 4.0.6 (Spring 7)
- **Language**: Java 21
- **Database**: PostgreSQL 16 (Hibernate 6 + Flyway)
- **Protocols**: REST (OpenAPI 3.0), gRPC (Spring gRPC Web)
- **Messaging**: RabbitMQ (High-throughput Event Ingestion)
- **Security**: Argon2 (Password Hashing), RSA-signed JWTs, Spring Security
- **Orchestration**: Docker & Docker Compose

## Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven (or use the provided `./mvnw` wrapper)

### Running Locally
This project utilizes Spring Boot's Docker Compose integration. When you run the application, it will automatically pull and start PostgreSQL and RabbitMQ containers.

1. **Start the Application:**
   ```bash
   ./mvnw clean spring-boot:run
   ```
2. **Accessing the APIs:**
   - **Swagger UI (REST)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **gRPC Endpoint**: `localhost:8080` (Supports Server Reflection)

### Default Administrator
On startup, Flyway will run all database migrations, and a bootstrap runner will automatically insert a default Admin user:
- **Email**: `admin@identio.com`
- **Password**: `admin123`

## 🗄 Database Migrations
We strictly use **Flyway** for database migrations. Do not rely on Hibernate's `ddl-auto` for production changes.
Migration scripts are located in `src/main/resources/db/migration/`.

## 🛡 Security Notes
- Passwords are never stored in plaintext; they are secured using Argon2.
- Session state is strictly disabled. Authentication is stateless using JWTs.
- Access tokens expire precisely at midnight (system time). Refresh tokens expire in 7 days.
- Ensure `private.pem` and `public.pem` (RSA keys that must be generated) are securely stored in a vault for production deployments.
