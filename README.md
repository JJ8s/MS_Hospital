# Sistema Hospitalario - Microservicios

## Integrantes
- Jordy Solis
- Anderson Huaiquinao 
- Matias Javier

## Descripción
Sistema hospitalario basado en arquitectura de microservicios
con Spring Boot, Eureka, API Gateway y JWT.

## Microservicios
| Servicio | Puerto | Responsable |
|---|---|---|
| eureka-hospital | 8761 | Anderson |
| api-gateway | 8080 | Anderson |
| ms-user | 8081 | Anderson |
| ms-auth | 8082 | Anderson |
| ms-medicos | 8083 | Jordy |
| ms-pacientes | 8084 | Jordy |
| ms-citas | 8085 | Jordy |
| ms-urgencia | 8086 | Jordy |
| ms-inventario | 8087 | Matias |
| ms-recetas | 8088 | Matias |
| ms-facturacion | 8090 | Matias |

## Pasos para ejecutar

### Requisitos previos
- Java 17 instalado
- IntelliJ IDEA instalado
- XAMPP instalado con MySQL activo
- Postman instalado

### 1. Configurar Base de Datos
1. Abrir XAMPP y iniciar **MySQL**
2. Las bases de datos se crean automáticamente al iniciar cada microservicio:
   - `db_user`
   - `db_medicos`
   - `db_pacientes`
   - `db_citas`
   - `db_urgencias`
   - `db_inventario`
   - `db_recetas`
   - `db_hospital_facturacion`

### 2. Orden de arranque en IntelliJ

#### Paso 1 — Servidor de descubrimiento
- Ejecutar `eureka-hospital`
- Verificar en: `http://localhost:8761`

#### Paso 2 — Usuarios y autenticación
- Ejecutar `ms-user` (puerto 8081)
- Ejecutar `ms-auth` (puerto 8082)

#### Paso 3 — API Gateway
- Ejecutar `api-gateway` (puerto 8080)

#### Paso 4 — Microservicios de dominio
- Ejecutar `ms-medicos` (puerto 8083)
- Ejecutar `ms-pacientes` (puerto 8084)
- Ejecutar `ms-citas` (puerto 8085)
- Ejecutar `ms-urgencia` (puerto 8086)

#### Paso 5 — Microservicios de recursos
- Ejecutar `ms-inventario` (puerto 8087)
- Ejecutar `ms-recetas` (puerto 8088)
- Ejecutar `ms-facturacion` (puerto 8090)

# Esperar que cada servicio muestre "Started" en consola antes de iniciar el siguiente.

### 3. Verificar registro en Eureka
- Abrir `http://localhost:8761`
- Confirmar que los 11 servicios aparecen como **UP**

### 4. Probar con Postman

#### Obtener token JWT
