FROM maven:3.8.6-jdk-11-slim
WORKDIR /app
COPY target/javapro37backend-0.0.1-SNAPSHOT.jar /app/backend.jar
COPY src/main/resources/application-deploy.yml /app/application.yml
RUN apt-get update && apt-get install -y fontconfig
ENTRYPOINT ["java", "-jar", "/app/backend.jar", "--spring.config.location=/app/application.yml"]
