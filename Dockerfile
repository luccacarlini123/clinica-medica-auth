FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar /app/clinica-medica-auth.jar

EXPOSE 8081

CMD [ "java", "-jar", "clinica-medica-auth.jar" ]