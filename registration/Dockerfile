# Set the base image
FROM maven as build

COPY . /app

RUN mvn -f app/pom.xml --batch-mode --update-snapshots verify


FROM openjdk

COPY --from=build /app/target/registration-SNAPSHOT-0.0.1.jar ./registration-SNAPSHOT-0.0.1.jar

CMD java -jar registration-SNAPSHOT-0.0.1.jar

EXPOSE 8096
