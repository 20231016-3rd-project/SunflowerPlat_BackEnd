FROM openjdk:11

WORKDIR /tcat

COPY sunflowerPlat-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]