FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/event-service-*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

