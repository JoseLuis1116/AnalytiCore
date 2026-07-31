FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && groupadd --gid 10001 appgroup \
    && useradd \
        --uid 10001 \
        --gid appgroup \
        --create-home \
        --home-dir /home/appuser \
        --shell /bin/false \
        appuser \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --chown=appuser:appgroup --from=build \
    /app/target/java-service-0.0.1-SNAPSHOT.jar \
    app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --retries=10 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]