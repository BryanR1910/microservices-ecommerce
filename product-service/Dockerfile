# ETAPA 1: BUILD (la construccion)
FROM maven:3.9.16-eclipse-temurin-25-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ETAPA 2: RUNTIME (el producto final
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar product.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "product.jar"]
