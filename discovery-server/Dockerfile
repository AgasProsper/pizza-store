FROM maven:3.9.3-amazoncorretto-20

RUN mkdir -p /opt/pizza-store/discovery-server
WORKDIR /opt/pizza-store/discovery-server

COPY pom.xml mvnw ./
COPY .mvn ./.mvn
COPY src ./src

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "/opt/pizza-store/discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar"]