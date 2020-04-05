FROM maven:3-alpine

ARG JAR_FILE=target/study-helper-bot.jar
ARG JAR_LIB_FILE=target/lib/

COPY pom.xml pipeline/

COPY src/ pipeline/src/

COPY ${JAR_FILE} app.jar

ADD ${JAR_LIB_FILE} lib/

WORKDIR pipeline/

RUN mvn clean install

ENTRYPOINT [ "java", "-jar", "app.jar"]