# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent pom and backend module pom for dependency resolution
COPY pom.xml /app/pom.xml
COPY backend/pom.xml /app/backend/pom.xml
RUN mvn -f /app/pom.xml -pl backend dependency:go-offline -B

# Copy backend source and package
COPY backend/src /app/backend/src
RUN mvn -f /app/pom.xml -pl backend package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/target/backend.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]
