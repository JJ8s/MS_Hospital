# Microservicio de Pacientes (Hospital)

Este microservicio gestiona la información de los pacientes siguiendo el patrón **CSR** (Controller-Service-Repository).

## Tecnologías
* Java 17+
* Spring Boot 3
* MySQL (Puerto 3306 / XAMPP)
* JPA / Hibernate
* Lombok & Jakarta Validation

## Endpoints Principales
* `GET /api/pacientes` - Listar todos.
* `POST /api/pacientes` - Registrar paciente (valida RUT y Edad).
* `GET /api/pacientes/rut/{rut}` - Buscar paciente por RUT.
* `PUT /api/pacientes/{id}` - Actualizar datos del paciente.

## Manejo de Errores
Se implementó un `GlobalExceptionHandler` para devolver respuestas JSON claras en caso de errores 404 o datos inválidos.
