# Dev image with Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21

# Workdir inside the container
WORKDIR /app

# Pre-cache dependencies for faster cold starts
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# Bring the rest of the project (you'll also bind-mount it in compose)
COPY src ./src

# Expose app + debug ports
EXPOSE 8080 5005

# Run Spring Boot in dev with fast restarts + remote debug
# - fork=false keeps the process in foreground for signals
# - additional-paths watches resources
# - jvmArguments opens JDWP on 0.0.0.0:5005
CMD ["mvn", "-Dspring-boot.run.profiles=dev", "-Dspring-boot.run.fork=false", "-Dspring-boot.run.additional-paths=src/main/resources", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "spring-boot:run"]