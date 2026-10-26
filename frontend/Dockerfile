# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent pom and frontend module pom for dependency resolution
COPY pom.xml /app/pom.xml
COPY frontend/pom.xml /app/frontend/pom.xml
RUN mvn -f /app/pom.xml -pl frontend dependency:go-offline -B

# Copy frontend source and package
COPY frontend/src /app/frontend/src
RUN mvn -f /app/pom.xml -pl frontend package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/frontend/target/frontend.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
