# Stage 1: Build the application with Gradle
FROM gradle:8.5 as builder

COPY . /home/build/project
WORKDIR /home/build/project
RUN gradle build -x test

# Extract the layers for Spring Boot's layered JAR
RUN java -Djarmode=layertools -jar build/libs/coursework-e2e-framework-0.0.1-SNAPSHOT.jar extract

# Stage 2: Run the application with OpenJDK
FROM openjdk:21

WORKDIR /app

# Copy the extracted layers from the builder stage
COPY --from=builder /home/build/project/dependencies/ ./
COPY --from=builder /home/build/project/snapshot-dependencies/ ./
COPY --from=builder /home/build/project/spring-boot-loader/ ./
COPY --from=builder /home/build/project/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]