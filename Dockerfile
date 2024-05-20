FROM gradle:8.5 as builder

COPY . /home/build/project
WORKDIR /home/build/project
RUN gradle build -x test

COPY build/libs/coursework-e2e-framework-0.0.1-SNAPSHOT.jar  app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
