FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=build/libs/Customer-Service.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/app.jar"]