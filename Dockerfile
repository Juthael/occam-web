FROM adoptopenjdk/openjdk11

RUN apt-get -y update && apt-get install -y graphviz && rm -rf /var/lib/apt/lists/*

ADD examples /examples

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]