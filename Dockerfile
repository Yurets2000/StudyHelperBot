FROM maven:3-alpine

ARG JAR_FILE=/pipeline/target/study-helper-bot.jar
ARG JAR_LIB_FILE=/pipeline/target/lib/

COPY pom.xml pipeline/

COPY src/ pipeline/src/

WORKDIR pipeline/

RUN mvn clean install

COPY ${JAR_FILE} app.jar

ADD ${JAR_LIB_FILE} lib/

ENTRYPOINT [ "java", "-jar", "app.jar"]