# Guia rapida Docker - MS Hospital

Estos archivos permiten levantar el proyecto completo con Docker:

- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`

Van en la raiz del repositorio, al mismo nivel que `api-gateway`, `eureka-hospital`, `ms-auth`, `ms-user`, etc.

## 1. Requisitos

- Docker Desktop instalado.
- Docker Desktop abierto antes de ejecutar comandos.
- Ejecutar comandos desde la raiz del proyecto.

## 2. Levantar todo

```bash
docker compose up --build
```

La primera vez puede demorar porque descarga Java, Maven, MySQL y dependencias Maven.

## 3. Levantar en segundo plano

```bash
docker compose up --build -d
```

## 4. Ver estado

```bash
docker compose ps
```

## 5. Ver logs

Todos los servicios:

```bash
docker compose logs -f
```

Un microservicio especifico:

```bash
docker compose logs -f ms-medicos
```

## 6. Reconstruir un solo microservicio

Ejemplo con medicos:

```bash
docker compose up --build ms-medicos
```

Ejemplo con pacientes:

```bash
docker compose up --build ms-pacientes
```

## 7. Apagar

```bash
docker compose down
```

Apagar y borrar la base de datos creada por Docker:

```bash
docker compose down -v
```

## 8. URLs directas

| Servicio | URL |
| --- | --- |
| Eureka | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| ms-user Swagger | http://localhost:8081/swagger-ui.html |
| ms-auth Swagger | http://localhost:8082/swagger-ui.html |
| ms-medicos Swagger | http://localhost:8083/swagger-ui.html |
| ms-pacientes Swagger | http://localhost:8084/swagger-ui.html |
| ms-citas Swagger | http://localhost:8085/swagger-ui.html |
| ms-urgencia Swagger | http://localhost:8086/swagger-ui.html |
| ms-inventario Swagger | http://localhost:8087/swagger-ui.html |
| ms-recetas Swagger | http://localhost:8088/swagger-ui.html |
| ms-facturacion Swagger | http://localhost:8090/swagger-ui.html |
| ms-notificaciones Swagger | http://localhost:8091/swagger-ui.html |

## 9. Rutas por API Gateway

| Microservicio | Ruta gateway |
| --- | --- |
| ms-user | http://localhost:8080/api/users |
| ms-auth | http://localhost:8080/api/auth |
| ms-medicos | http://localhost:8080/api/medicos |
| ms-pacientes | http://localhost:8080/api/pacientes |
| ms-citas | http://localhost:8080/api/citas |
| ms-urgencia | http://localhost:8080/api/urgencias |
| ms-inventario | http://localhost:8080/api/productos |
| ms-recetas | http://localhost:8080/api/recetas |
| ms-facturacion | http://localhost:8080/api/facturas |
| ms-notificaciones | http://localhost:8080/api/notificaciones |

## 10. Base de datos

Docker levanta un MySQL propio:

| Dato | Valor |
| --- | --- |
| Host desde tu PC | localhost |
| Puerto desde tu PC | 3307 |
| Host dentro de Docker | mysql |
| Puerto dentro de Docker | 3306 |
| Usuario | root |
| Password | root |

Si XAMPP ya esta usando el puerto `3307`, cambia en `docker-compose.yml` esta linea:

```yaml
ports:
  - "3307:3306"
```

Por ejemplo:

```yaml
ports:
  - "3308:3306"
```

## 11. Tests unitarios

Los 10 microservicios tienen test de service y controller:

| Microservicio | Service test | Controller test |
| --- | --- | --- |
| ms-auth | Si | Si |
| ms-user | Si | Si |
| ms-medicos-main | Si | Si |
| ms-pacientes-main | Si | Si |
| ms-citas | Si | Si |
| ms-urgencia | Si | Si |
| ms-inventario | Si | Si |
| ms-recetas | Si | Si |
| ms-facturacion | Si | Si |
| ms-notificaciones | Si | Si |

Para correr los tests en Windows, entra a cada microservicio y ejecuta:

```bash
mvn test
```

Ejemplo:

```bash
cd ms-medicos-main
mvn test
```

## 12. Nota importante

El `Dockerfile` compila con:

```bash
mvn -B -DskipTests package
```

Esto es normal para construir imagenes Docker rapido. Los tests se corren antes con `mvn test`.
