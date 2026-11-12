# ── Stage 1: Build ──
# Uses pre-installed Maven with Eclipse Temurin JDK 17 on Alpine.
# Pre-installed Maven guarantees reliable builds without needing curl/wget wrapper downloads.
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml first to take advantage of Docker layer caching
COPY pom.xml .
RUN mvn dependency:resolve -B

# Copy source code and build production JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ──
# Ultra-lightweight JRE-only image for deployment
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render / Railway dynamically assigns the PORT environment variable
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
