FROM openjdk:11-jre-slim
ARG JAR_FILE=build/libs/*.jar

COPY sunflowerPlat-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]