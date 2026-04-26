# Use Java 21 runtime
FROM eclipse-temurin:21-jdk
LABEL authors="KishoreVanam"

# Copy JAR file
COPY target/order-service.jar app.jar

# Expose port (adjust to your app)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]