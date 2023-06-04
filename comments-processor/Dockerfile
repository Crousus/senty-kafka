# Dockerfile

# Start with a base image containing Java runtime and Maven
FROM ubuntu:22.04

#install java and maven
RUN apt-get update && apt-get install -y openjdk-11-jdk maven

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml ./
COPY src ./src/

# Build the application with Maven
RUN mvn dependency:purge-local-repository

RUN mvn clean package


RUN apt-get update && apt-get install -y libstdc++6

# Add a volume pointing to /tmp
WORKDIR /app/target

# Make port 7001 available to the world outside this container
EXPOSE 7000

# Run the jar file
CMD ["java", "-jar", "comment-processor-1.0-SNAPSHOT.jar"]
