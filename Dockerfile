# ---- Build stage ----
FROM docker.io/library/maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build
COPY .mvn ./.mvn
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Runtime stage ----
FROM docker.io/library/eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/target/aperture-*.jar app.jar
ENV APP_DATABASE_DIRECTORY=/data
ENV SERVER_PORT=8080
ENV APP_API_KEY=your-default-dev-key
VOLUME ["/data"]
EXPOSE 8080
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
