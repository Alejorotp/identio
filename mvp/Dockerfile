# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy the maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Give execution rights to the maven wrapper
RUN chmod +x ./mvnw

# Download dependencies (this step is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application jar
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080 (REST) and 9090 (gRPC default)
EXPOSE 8080
EXPOSE 9090

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
