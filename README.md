# SecureFiles

A secure file management application built with Spring Boot (backend) and React (frontend) using (simplified) hexagonal architecture.

Find all the sources for IA prompts in `BUISNESS_SPEC.md`, `AGENTS.md` and `PROMPTS.md`
Find the approach used for the project in `DETAILED_APPROACH.md`

## Architecture

This project follows hexagonal architecture principles:
- **Application**: Use cases and ports interfaces
- **Domain**: Core business logic and entities
- **Controllers**: REST API endpoints
- **Infrastructure**: Antivirus, Repositories, Storage
- **Exceptions**: Custom exceptions for error handling

## Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- Docker and Docker Compose

Replace the data in the `.env.example` file with your own values for database credentials.
Rename the file to `.env`. It should stay in the root directory of the project.

### Running with Docker
```bash
docker-compose up --build
```

### Running Locally
1. Start the backend:
   ```bash
   cd backend
   ./mvnw.cmd spring-boot:run
   ```

2. Start the frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Development
- Backend: Spring Boot with JPA
- Frontend: React with Vite and Ant Design for UI components
- Database: H2 for development, PostgreSQL for production
- Storage : MinIO for file storage
- Authentication: No authentication implemented yet, but can be added using Spring Security and JWT. Maybe if I'm motivated next Saturday at 6 am :).
- Antivirus : ClamAV for scanning files before upload (no infected files will be stored, neither  will be the metadata of the file).
