# 1. Usamos una imagen ligera de Java 17
FROM eclipse-temurin:17-jdk-alpine

# 2. Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiamos el archivo .jar generado por Maven al contenedor
COPY target/*.jar app.jar

# 4. Informamos el puerto real en el que corre ms-medicos
EXPOSE 8083

# 5. Comando definitivo para arrancar el microservicio (Sin comentarios encima)
ENTRYPOINT ["java", "-jar", "app.jar"]