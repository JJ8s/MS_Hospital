FROM maven:3.9.9-eclipse-temurin-17 AS build

ARG SERVICE_DIR

WORKDIR /build
COPY ${SERVICE_DIR}/ ./

RUN mvn -B -DskipTests package \
    && JAR_FILE="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*original*' | head -n 1)" \
    && cp "$JAR_FILE" /tmp/app.jar

FROM eclipse-temurin:17-jre

ARG APP_PORT=8080

WORKDIR /app
COPY --from=build /tmp/app.jar app.jar

EXPOSE ${APP_PORT}

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
