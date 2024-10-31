# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the jar file from the target directory to the working directory in the container
COPY target/integration-service-0.0.1-SNAPSHOT.jar integration-service-0.0.1-SNAPSHOT.jar

ENV CONNECTION_STRING_INTEGRATION_POSTGRES_PROD=${CONNECTION_STRING_INTEGRATION_POSTGRES_PROD}
ENV POSTGRES_USERNAME=${USERNAME_INTEGRATION_POSTGRES_PROD}
ENV POSTGRES_PASSWORD=${PASSWORD_INTEGRATION_POSTGRES_PROD}
ENV KAFKA_BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVER}
ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PASSWORD=${REDIS_PASSWORD}
ENV REDIS_PORT=${REDIS_PORT}
ENV REDIS_DATABASE=${REDIS_DATABASE}
# Expose the port the application runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "integration-service-0.0.1-SNAPSHOT.jar"]
