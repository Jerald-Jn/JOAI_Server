# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/JoAI-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]