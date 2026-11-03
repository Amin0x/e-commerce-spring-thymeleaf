# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the project files and resolve dependencies
COPY pom.xml /app/pom.xml
RUN mvn -f /app/pom.xml dependency:go-offline -B

# Copy the source and package
COPY src /app/src
RUN mvn -f /app/pom.xml package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/frontend.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
