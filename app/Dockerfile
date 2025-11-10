# Multi-stage build para Spring Boot
# Etapa de build
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Copia arquivos Maven Wrapper e POM primeiro para cache de dependências
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml

# Download das dependências (cache layer)
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

# Copia código fonte
COPY src src

# Build do pacote (omitindo testes para velocidade – ajuste se quiser rodar testes)
RUN ./mvnw -q package -DskipTests

# Etapa de runtime (imagem menor)
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Copia o jar gerado
COPY --from=build /workspace/target/app-0.0.1-SNAPSHOT.jar app.jar

# Porta padrão Spring Boot
EXPOSE 8080

# Variáveis padrão podem ser sobrescritas pelo compose
ENV JAVA_OPTS=""

# Entrada da aplicação
ENTRYPOINT ["bash", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
