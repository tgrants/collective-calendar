FROM docker.io/library/maven:4.0.0-rc-5-eclipse-temurin-25-alpine AS build
WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
	mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
	mvn clean package -DskipTests

FROM docker.io/library/eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
