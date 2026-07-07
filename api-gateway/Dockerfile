# Usamos una imagen ligera de Java 17
FROM eclipse-temurin:17-jdk-alpine

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo .jar generado por Maven al contenedor
COPY target/*.jar app.jar

# Exponemos el puerto (informativo, Compose se encargará de mapearlo)
ENTRYPOINT ["java", "-jar", "app.jar"]