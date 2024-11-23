# Stage 1: Build the application using Maven
FROM maven as build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY pom.xml /app
COPY src /app/src

# Run the Maven build and package the Spring Boot application (skip tests to speed up the process)
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17

# Set the working directory for the runtime image
WORKDIR /app

# Copy the packaged jar from the build stage into the runtime container
COPY --from=build /app/target/e-commerce-spring-thymeleaf.jar /app/app.jar

# Expose the port on which the Spring Boot app will run (default is 8080)
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "/app/app.jar"]
