# Microservicio de Médicos (ms-medicos) 🏥

Este microservicio gestiona la información de los profesionales de salud, permitiendo operaciones CRUD y búsquedas especializadas.

## Tecnologías
* **Java 17 / Spring Boot**
* **MySQL**
* **Spring Data JPA**
* **Eureka Client**

## Endpoints Principales
* `GET /api/medicos`: Listar todos.
* `POST /api/medicos`: Registrar médico.
* `GET /api/medicos/rut/{rut}`: Buscar por RUT único.
* `GET /api/medicos/especialidad/{especialidad}`: Buscar lista por área.

## Configuración
El servicio corre en el puerto `8082` y se registra en Eureka Server en el puerto `8761`.
