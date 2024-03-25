################################################################################

# Create a stage for resolving and downloading dependencies.
FROM eclipse-temurin:11-jdk-jammy as deps

WORKDIR /build

# Copy the mvnw wrapper for the Spring Boot application.
COPY mvnw mvnw
COPY .mvn/ .mvn/

# Download dependencies for the Spring Boot application.
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests

################################################################################

# Create a stage for building the Spring Boot application.
FROM deps as package

WORKDIR /build

COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar app.jar

################################################################################

# Create a stage for setting up the PostgreSQL database.
FROM postgres:latest AS postgresql

# Copy the initialization script for setting up the database.
COPY database/tenmo.sql /docker-entrypoint-initdb.d/tenmo.sql

################################################################################

# Create a new stage for running the Spring Boot application.
FROM eclipse-temurin:11-jre-jammy AS final

# Set the working directory
WORKDIR /app

# Copy the packaged Spring Boot application from the previous stage
COPY --from=package /build/app.jar .

# Expose the port on which the Spring Boot application will run
EXPOSE 8080

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]