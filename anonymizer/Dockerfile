FROM openjdk:11


RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY pom.xml /app/
COPY src/ /app/src

RUN mvn package

FROM openjdk:11
RUN apt-get -q update && apt-get -qy install netcat
COPY --from=0 /app/target/anonymizer-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY wait-for .

ENTRYPOINT ["java", "-jar", "app.jar"]