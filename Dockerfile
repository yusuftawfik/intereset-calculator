# ------------------------------------------------------------
# Stage 1: the "workshop". Has Java and Maven, builds the app
# (this also runs all the tests; if one fails, the build stops).
# ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn -B package

# ------------------------------------------------------------
# Stage 2: the "showroom". Only Java, plus the finished app.
# Much smaller, because the build tools are left behind.
# ------------------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run the app as a normal user instead of the all-powerful admin user
RUN useradd --system --uid 1001 appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# MaxRAMPercentage keeps Java from using more memory than the free server has
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
