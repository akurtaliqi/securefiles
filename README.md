# SecureFiles

A secure file management application built with Spring Boot (backend) and React (frontend) using hexagonal architecture.

## Architecture

This project follows hexagonal architecture principles:
- **Domain Layer**: Core business logic
- **Application Layer**: Use cases and services
- **Adapter Layer**: Controllers and repositories
- **Configuration Layer**: Dependency injection

## Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- Docker and Docker Compose

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
- Backend: Spring Boot with JPA and H2/PostgreSQL
- Frontend: React with Vite
- Database: H2 for development, PostgreSQL for production
