# Step 1: Java 17 Base Image (LTS version)
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Container ke andar 'app' directory banao
WORKDIR /app

# Step 3: Tumhari exact JAR file ko copy karna
COPY target/tpms-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Spring Boot ka port 8080 expose karna
EXPOSE 8080

# Step 5: Application run karne ki command
ENTRYPOINT ["java", "-jar", "app.jar"]