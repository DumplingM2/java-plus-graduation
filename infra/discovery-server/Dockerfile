FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/discovery-server-*.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]

