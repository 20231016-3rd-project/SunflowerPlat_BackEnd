FROM openjdk:11-jdk
COPY sunflowerPlate-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
RUN yum install -y tzdata
RUN timedatectl set-timezone Asia/Seoul
