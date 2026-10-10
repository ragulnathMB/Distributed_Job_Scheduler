# One Dockerfile for both node types:  docker build --build-arg MODULE=scheduler .   |   --build-arg MODULE=worker .
FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE
WORKDIR /build

# Cache dependencies first
COPY pom.xml .
COPY common/pom.xml common/
COPY scheduler/pom.xml scheduler/
COPY worker/pom.xml worker/
RUN mvn -q -B -pl ${MODULE} -am dependency:go-offline || true

COPY common common
COPY scheduler scheduler
COPY worker worker
RUN mvn -q -B -pl ${MODULE} -am -DskipTests package

FROM eclipse-temurin:17-jre
ARG MODULE
WORKDIR /app
COPY --from=build /build/${MODULE}/target/app.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
