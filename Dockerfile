
FROM eclipse-temurin:17-jdk-alpine

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY . .
RUN mvn clean package -DskipTests

COPY target/velocitymedia-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
