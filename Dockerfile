# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

# cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# copy source
COPY src ./src

# build jar
RUN mvn clean package -DskipTests


# ---------- Stage 2: Run ----------
FROM eclipse-temurin:25-jre

WORKDIR /app

# copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Railway will inject PORT
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh","-c","java -Dserver.port=$PORT -jar app.jar"]