# Etapa 1: Build
FROM ubuntu:latest AS build

# Atualiza e instala Java 17 e Maven
RUN apt-get update && apt-get install openjdk-17-jdk maven -y

# Define a pasta de trabalho
WORKDIR /app

# Copia os arquivos
COPY . .

# Faz o build
RUN mvn clean install -DskipTests

# Etapa 2: Run (Imagem leve)
FROM eclipse-temurin:17-jdk-alpine

EXPOSE 8080

# Copia o JAR
COPY --from=build /app/target/todolist-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]