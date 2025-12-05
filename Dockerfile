# Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Ejecutar la clase principal que hemos creado
CMD ["java", "-cp", "app.jar", "BiCIAMApp"]
