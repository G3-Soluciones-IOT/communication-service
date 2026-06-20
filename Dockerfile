FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY target/*.jar /app/app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

