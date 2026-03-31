# ── Stage 1 : build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy the pom.xml file to leverage dependency caching
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline -B

# Copy the source code and compile the application
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2 : runtime ─────────────────────────────────────────────────────────
FROM sourcemation/jdk-21

WORKDIR /app

COPY --from=builder /build/target/securefiles-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]