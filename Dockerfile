FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM maven:3.8.5-openjdk-17
WORKDIR /app

COPY --from=build /app/target/gerenciador-de-veiculos-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
