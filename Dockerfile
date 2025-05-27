FROM openjdk:21-jdk

LABEL authors="lunaxion <lunaof12@naver.com>"

WORKDIR /app

ARG JAR_FILE=build/libs/lunaproject-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# 7. 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]