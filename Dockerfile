# --- ESTÁGIO 1: Build da aplicação ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven e o wrapper
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Baixa as dependências (otimiza o cache do Docker)
RUN ./mvnw dependency:go-offline

# Copia o código-fonte
COPY src src

# Compila o projeto gerando o .jar (pulando testes para acelerar)
RUN ./mvnw clean package -DskipTests

# --- ESTÁGIO 2: Execução da aplicação ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o arquivo .jar compilado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão da aplicação Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]