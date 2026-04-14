# ==================== DEVELOPMENT Dockerfile ====================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Cài Maven wrapper
COPY .mvn/ .mvn
COPY mvnw .
RUN chmod +x mvnw

# Copy pom.xml trước để cache dependencies
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Expose port
EXPOSE 8080

# Command để chạy với hot-reload (dev)
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=dev"]