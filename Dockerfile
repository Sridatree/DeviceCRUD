FROM eclipse-temurin:21 AS build

# Work directory for the build
WORKDIR /app



COPY pom.xml .

COPY mvnw .
COPY .mvn ./.mvn

RUN chmod +x mvnw
RUN ./mvnw -q dependency:go-offline

COPY src ./src
RUN ./mvnw -q clean package -DskipTests

FROM eclipse-temurin:21 AS runtime

WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

#App port
EXPOSE 8080

ARG MONGODB_URI="mongodb+srv://sridatree70:8K4h0qxDJQvpXp24@testcluster.bhqmzqa.mongodb.net/devicedb"

ENV SPRING_DATA_MONGODB_URI=${MONGODB_URI}

ENTRYPOINT ["java","-Djavax.net.debug=ssl,handshake","-jar","/app/app.jar"]

