# Dockerfile

# Start with a base image containing Java runtime and Maven
FROM maven:3.8.5-eclipse-temurin-17-alpine AS build

# The application's base directory
WORKDIR /spring-boot-service

# Add the source code to the container
COPY src /spring-boot-service/src
COPY pom.xml /spring-boot-service

RUN mv src/main/resources/application-docker.properties src/main/resources/application.properties

# Package the application
RUN mvn clean package -DskipTests

# Use openjdk for runtime
FROM eclipse-temurin:17-jdk-alpine AS runtime

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8095 available to the world outside this container
EXPOSE 8095

# Copy the jar file from the build image
COPY --from=build /spring-boot-service/target/*.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]