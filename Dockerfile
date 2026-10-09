# --- ESTÁGIO 1: Build da aplicação ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven e o wrapper
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]