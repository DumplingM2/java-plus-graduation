FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/config-server-*.jar app.jar

EXPOSE 8888

CMD ["java", "-jar", "app.jar"]

