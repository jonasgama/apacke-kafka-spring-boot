FROM openjdk:8 AS BUILD_IMAGE
RUN mkdir -p /root/dev/myapp
COPY . /root/dev/myapp
WORKDIR /root/dev/myapp

# download dependencies
RUN ./gradlew build -x test

FROM openjdk:8-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/dev/myapp/build/libs/kafka-consumer-0.0.1-SNAPSHOT.jar ./myapp.jar
EXPOSE 8081
CMD ["java","-jar","myapp.jar"]

