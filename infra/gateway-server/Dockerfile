FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/gateway-server-*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

