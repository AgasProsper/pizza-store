FROM maven:3.9.3-amazoncorretto-20

# ARG build_type=dev

WORKDIR .

COPY ./target/api-gateway-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "api-gateway-0.0.1-SNAPSHOT.jar"]
