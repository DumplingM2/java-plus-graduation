FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/user-service-*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

