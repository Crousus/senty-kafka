FROM maven as build

COPY . /app

RUN mvn -f app/pom.xml --batch-mode --update-snapshots verify -DskipTests

FROM openjdk

COPY --from=build /app/target/checkout-0.0.1-SNAPSHOT.jar ./checkout-0.0.1-SNAPSHOT.jar

COPY --from=build /app/src/main/resources/application.properties ./application.properties

CMD java -jar checkout-0.0.1-SNAPSHOT.jar --spring.config.location=application.properties
