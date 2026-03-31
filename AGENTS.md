# AI Agent Guidelines for SecureFiles

## Business Requirements
See [BUSINESS_SPEC.md](BUSINESS_SPEC.md) for detailed business specifications and project objectives.

## Architecture Overview
- **Framework**: Spring Boot 4.0.5 with Java 21
- **Data Layer**: JPA entities with H2 in-memory database for development
- **Web Layer**: Spring Web MVC for REST endpoints
- **Key Dependencies**: Lombok for boilerplate reduction, H2 console for DB inspection

## Hexagonal Architecture
- **Domain Layer**: Core business logic in `domain/` package - entities, value objects, domain services, ports (interfaces)
  - **Entities**: `FileMetadata`, `FileStatus`
  - **Enums**: `FileStatus` (file lifecycle states)
- **Application Layer**: Use cases and application services in `application/` package (business orchestration)
- **Controller Layer**: HTTP layer only in `controller/` package
- **Infrastructure Layer**: External systems in `infrastructure/` package (DB, storage, antivirus)
- **Package Structure**: `com.praxedo.securefiles.{application,controller,domain,infrastructure}`

## Project Structure
- `secure-file-service/` (project root)
- `backend/` contains the Maven project root
- `frontend/` contains the React frontend application
- Source code: `backend/src/main/java/com/praxedo/securefiles/`
- Resources: `backend/src/main/resources/`
- Tests: `backend/src/test/java/com/praxedo/securefiles/`
- Frontend source: `frontend/src/`

## Development Workflows
- **Build Backend**: `./mvnw.cmd clean compile`
- **Run Application**: `./mvnw.cmd spring-boot:run` or `docker-compose up`
- **Run Tests**: `./mvnw.cmd test`
- **Package JAR**: `./mvnw.cmd package`
- **H2 Console**: Access at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)
- **Frontend Dev**: `cd frontend && npm run dev`
- **Docker Build**: `docker-compose build`

## Coding Conventions
- **Lombok Usage**: Prefer `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` for entities; `@RequiredArgsConstructor` for services
- **DTO Usage**: Create separate DTO classes in `controller/dto/` package for request/response mapping; 
- **Lombok** `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` for clean implementation; 
- **MapStruct** for entity-to-DTO conversion
- **Package Naming**: Follow `com.praxedo.securefiles.{domain,application,controller,infrastructure}`
- **Configuration**: Minimal properties in `application.properties`; use profiles for environments
- **Entity Example**: Use `@Entity @Table(name="files")` with `@Id @GeneratedValue(strategy=GenerationType.IDENTITY)`
- **Controller Example**: `@RestController @RequestMapping("/api/files")` with standard CRUD mappings
- **Unused Code**: Never add unused code; ensure all code is actively used or remove it
- **Language** : All code must be in English; no comments or identifiers in other languages

## Key Files
- `BUSINESS_SPEC.md`: Business requirements and project objectives
- `pom.xml`: Dependency management and build config
- `SecurefilesApplication.java`: Main entry point
- `application.yml`: Configuration (replaces properties)
- `docker-compose.yml`: Multi-container setup
- `.env`: Environment variables (located in backend/)
- `README.md`: Project documentation
- `frontend/package.json`: Frontend dependencies
- `frontend/vite.config.js`: Vite configuration

## Integration Points
- **Database**: H2 for dev; configure external DB in properties for prod
- **Security**: Not implemented yet; expect Spring Security for file access control
- **File Handling**: Anticipate multipart uploads; use `MultipartFile` in controllers

## Patterns to Follow
- **Repository Pattern**: Extend `JpaRepository<Entity, ID>` for data access
- **Service Layer**: `@Service` classes for business logic
- **Exception Handling**: Use `@ControllerAdvice` for global error responses
- **Validation**: `@Valid` on request bodies with Bean Validation annotations

# Backend Development Guidelines
## 🎯 Objective
Build a **simple, clean, maintainable microservice** for secure file handling.

Focus on:
* clarity over cleverness
* simplicity over completeness
* correctness over features

---

## 🧱 Architecture Principles
* Follow a **layered / hexagonal-inspired architecture**:
  * `controller` → HTTP layer only
  * `application` → use cases (business orchestration)
  * `domain` → core models (no framework dependency)
  * `infrastructure` → external systems (DB, storage, antivirus)

* Strict separation of concerns:
  * No business logic in controllers
  * No framework logic in domain
  * Infrastructure must be replaceable

---

## 🧠 Design Rules
### 1. Keep it simple (KISS)
* Avoid over-engineering
* Do not introduce abstractions without clear need
* Prefer straightforward implementations

### 2. One responsibility per class
* Each class should do one thing well (SRP)
* Use cases should represent a single business action
* Do not mix concerns (e.g., scanning logic should not be in the upload service)
* Do not create "utility" classes that do multiple unrelated things

### 3. No premature features
Do NOT implement:
* authentication
* authorization
* messaging systems (Kafka, RabbitMQ)
* caching
* complex configuration

### 4. Business rule priority
The core rule MUST always be enforced:
> A file must never be downloadable unless it has been successfully scanned.

### 5. State management

File lifecycle:

```
UPLOADED → SCANNING → CLEAN | INFECTED
```

* State transitions must be explicit and controlled
* Never bypass state checks

---

## 💾 Persistence Rules
* Use H2 for simplicity
* Store only **metadata in database**
* Never store file binaries in the database
* Files must be stored in a dedicated storage system

## 1. File Handling
* Always stream files (no full memory load)
* Generate unique storage paths (UUID-based)
* Do not trust original filenames

## 2. External Dependencies
All external systems must be abstracted:
* Storage → `FileStoragePort`
* Antivirus → `AntivirusPort`

Implementations must be replaceable without impacting business logic.

## ⚙️ Async Processing
* Antivirus scanning must be asynchronous
* Do not block upload requests
* Use simple async mechanisms (`@Async`) — no queues required

---

## 🌐 API Design
* RESTful endpoints only
* Use simple and clear contracts
* Implement endpoint documentation (e.g., Swagger/OpenAPI, Responses, DTOs)
* Always check endpoint inputs for validity and handle errors gracefully

Required endpoints:
* `POST /files` → upload

---

## ❌ Error Handling
* Fail fast
* Use meaningful exceptions
* Do not expose internal errors

---

## 🧪 Testing Guidelines
* Write unit tests for:

  * use cases
  * business rules

* Mock external dependencies:

  * storage
  * antivirus

* Do NOT test frameworks

---

## 🧹 Code Quality
* Use meaningful names (no abbreviations)
* Avoid comments explaining obvious code
* Prefer readable code over clever code

---

## 🚫 What to Avoid
* Over-abstraction
* Complex patterns (CQRS, Event Sourcing, etc.)
* Unused code
* Dead code
* Premature optimization

---

## 🧠 Decision Philosophy
When in doubt:

> Choose the simplest solution that satisfies the requirement.

---

## 🚀 Scalability Considerations (Do NOT implement, only design for)
* Replace H2 with PostgreSQL
* Replace local storage with S3/MinIO
* Replace async with message queue

---

## 📌 Final Rule
The system must be:
* runnable in one command
* understandable in minutes
* modifiable without fear

---

## 💬 AI Usage
AI-generated code must:
* respect these guidelines
* be reviewed before integration
* be simplified if overly complex
* Never generate code that is not immediately used
* Never anticipate future features
* Never create more than requested
* Prefer fewer classes over more abstractions
* If a class is not used, do not create it