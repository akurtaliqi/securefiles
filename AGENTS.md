# AI Agent Guidelines for SecureFiles

## Business Requirements
See [BUSINESS_SPEC.md](BUSINESS_SPEC.md) for detailed business specifications and project objectives.

## Developer Profile
- **Role**: Software Engineer
- **Applying for**: Senior Fullstack Developer
- **Experience**: 5 years of professional experience + 3 years of part-time professional experience during bachelor and master studies
- **Focus**: Clean code oriented

## Architecture Overview
- **Framework**: Spring Boot 4.0.5 with Java 21
- **Data Layer**: JPA entities with H2 in-memory database for development
- **Web Layer**: Spring Web MVC for REST endpoints
- **Key Dependencies**: Lombok for boilerplate reduction, H2 console for DB inspection

## Hexagonal Architecture
- **Domain Layer**: Core business logic in `domain/` package - entities, value objects, domain services, ports (interfaces)
- **Application Layer**: Use cases and application services in `application/` package
- **Adapter Layer**: 
  - **Driving Adapters** (primary ports): REST controllers, CLI in `adapter/in/web/`
  - **Driven Adapters** (secondary ports): JPA repositories, external APIs in `adapter/out/persistence/`
- **Configuration Layer**: Dependency injection and bean configuration in `config/` package
- **Package Structure**: `com.praxedo.securefiles.{domain,application,adapter,config}`

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
- **Package Naming**: Follow `com.praxedo.securefiles.{domain}` (e.g., `entity`, `controller`, `service`)
- **Configuration**: Minimal properties in `application.properties`; use profiles for environments
- **Entity Example**: Use `@Entity @Table(name="files")` with `@Id @GeneratedValue(strategy=GenerationType.IDENTITY)`
- **Controller Example**: `@RestController @RequestMapping("/api/files")` with standard CRUD mappings

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
