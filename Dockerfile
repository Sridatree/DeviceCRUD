FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Work directory for the build
WORKDIR /app

COPY pom.xml .

COPY mvnw .
COPY .mvn ./.mvn

RUN ./mvnw -q dependency:go-offline

COPY src ./src
RUN ./mvnw -q clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

#App port
EXPOSE 8080

ARG MONGODB_URI=mongodb+srv://sridatree70:8K4h0qxDJQvpXp24@testcluster.bhqmzqa.mongodb.net/device-crud?retryWrites=true&w=majority
ENV SPRING_DATA_MONGODB_URI=${MONGODB_URI}

ENTRYPOINT ["java","-jar","/app/app.jar"]
