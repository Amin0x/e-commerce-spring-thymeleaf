# Use a base image with Java 11 or 17
FROM openjdk:17-jdk-slim as build

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/e-commerce-spring-thymeleaf.jar /app/app.jar

# Expose the port on which the Spring Boot app will run (default is 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
