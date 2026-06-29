# Sistema de Gestión Hospitalaria - Arquitectura de Microservicios

## Descripción

Este repositorio contiene el desarrollo del backend de un **Sistema de Gestión Hospitalaria** construido bajo una arquitectura de microservicios utilizando **Spring Boot**, **Spring Cloud** y **Java 17**.

El objetivo del proyecto es distribuir las responsabilidades del sistema en servicios independientes que puedan comunicarse entre sí mediante REST, facilitando el mantenimiento, escalabilidad y reutilización de los distintos módulos del sistema.

El flujo principal implementado permite registrar productos médicos, generar recetas y emitir facturas considerando tanto el costo del servicio médico como el valor de los medicamentos utilizados.

---

# Autor

**Matías Currin**

Desarrollador Backend

Microservicios desarrollados:

* ms-inventario
* ms-recetas
* ms-facturación

---

# Arquitectura General

El sistema fue diseñado siguiendo una arquitectura distribuida basada en microservicios.

Cada microservicio posee:

* Responsabilidad única.
* Base de código independiente.
* Configuración propia.
* Registro automático en Eureka.
* Comunicación mediante REST.

Arquitectura general:

```
Cliente

      │

API Gateway

      │

Eureka Server

      │

───────────────────────────────

ms-inventario

ms-recetas

ms-facturación
```

Esta estructura permite desacoplar los servicios y simplificar futuras ampliaciones del sistema.

---

# Componentes de Infraestructura

## Eureka Server

Puerto: **8761**

Responsable del descubrimiento de servicios.

Funciones principales:

* Registrar automáticamente cada microservicio.
* Permitir que los servicios se encuentren dinámicamente.
* Evitar el uso de direcciones IP o puertos fijos durante la comunicación.

---

## API Gateway

Puerto: **8080**

Representa el único punto de entrada al sistema.

Su función es:

* Centralizar las solicitudes HTTP.
* Redireccionar las peticiones al microservicio correspondiente.
* Simplificar el acceso desde el cliente.
* Mantener ocultos los puertos internos de cada servicio.

Ejemplo:

```
Cliente

↓

localhost:8080/api/...

↓

Gateway

↓

Microservicio correspondiente
```

---

# Microservicios

## ms-inventario

Responsable de administrar todos los medicamentos e insumos médicos.

Funciones principales

* Registrar productos.
* Consultar inventario.
* Actualizar información.
* Controlar stock.
* Eliminar registros.
* Validar disponibilidad de medicamentos.

Este servicio representa la fuente oficial del stock disponible dentro del sistema.

---

## ms-recetas

Gestiona las recetas médicas emitidas por los profesionales.

Responsabilidades

* Registrar recetas.
* Asociar paciente y médico.
* Validar información.
* Consultar recetas.
* Consumir información del inventario cuando es necesario.

Este servicio concentra únicamente la lógica relacionada con las recetas médicas.

---

## ms-facturación

Administra el proceso de facturación del hospital.

Funciones

* Registrar facturas.
* Calcular costo total.
* Obtener información desde otros servicios.
* Consolidar valores médicos y medicamentos.

La lógica de cálculo se mantiene completamente aislada del resto de microservicios.

---

# Comunicación entre Microservicios

La interoperabilidad del sistema se realiza mediante comunicación REST utilizando **Feign Client**.

Flujo implementado:

```
Facturación

↓

Recetas

↓

Inventario
```

Esta comunicación permite que cada microservicio mantenga su propia responsabilidad sin acceder directamente a la base de datos de otro servicio.

Cada servicio expone únicamente los endpoints necesarios para compartir información.

---

# Patrón de Desarrollo

Todos los microservicios siguen el patrón:

**Controller → Service → Repository**

### Controller

* Recibe solicitudes HTTP.
* Devuelve respuestas HTTP.
* No contiene lógica de negocio.

### Service

* Implementa las reglas del negocio.
* Realiza validaciones.
* Gestiona la comunicación con otros servicios.
* Controla excepciones.

### Repository

* Acceso a datos mediante Spring Data JPA.
* Operaciones CRUD.
* Sin lógica de negocio.

### Model

Representa las entidades persistentes del sistema.

Esta separación permite mantener un código desacoplado y facilita las pruebas unitarias.

---

# Configuración

Cada microservicio posee su propio archivo **application.yml**.

Entre las propiedades configuradas se encuentran:

* server.port
* spring.application.name
* datasource
* spring.jpa
* eureka.client
* logging

La configuración externa permite modificar el comportamiento de la aplicación sin alterar el código fuente.

---

# Documentación

Todos los microservicios incorporan documentación mediante **Swagger / OpenAPI**.

La documentación permite visualizar:

* Endpoints.
* Parámetros.
* Request Body.
* Response Body.
* Modelos.
* Códigos HTTP.

Esto facilita las pruebas y la integración entre servicios.

---

# Pruebas Unitarias

Las pruebas fueron implementadas utilizando:

* JUnit 5
* Mockito

Las pruebas se concentran principalmente sobre la capa Service para validar la lógica del negocio sin depender de la base de datos.

Se utilizan mocks para simular repositorios y dependencias externas.

---

# Flujo General del Sistema

1. Se registra un medicamento en Inventario.
2. Se crea una receta médica asociando paciente y medicamento.
3. El sistema valida la información correspondiente.
4. Se genera la factura considerando el costo del servicio médico y los medicamentos.
5. La respuesta es enviada al cliente mediante el API Gateway.

---

# Guía rápida de ejecución

Orden recomendado:

1. Levantar Eureka Server.
2. Levantar API Gateway.
3. Ejecutar ms-inventario.
4. Ejecutar ms-recetas.
5. Ejecutar ms-facturación.
6. Verificar el registro de todos los servicios en Eureka.
7. Probar los endpoints desde Swagger o Postman.

---

# Tecnologías utilizadas

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Cloud Gateway
* Spring Cloud OpenFeign
* Eureka Server
* Swagger / OpenAPI
* JUnit 5
* Mockito
* Maven

---

# Consideraciones de Diseño

Durante el desarrollo se buscó mantener una arquitectura desacoplada, escalable y fácil de mantener.

Las principales decisiones técnicas fueron:

* Aplicación del patrón Controller-Service-Repository.
* Separación de responsabilidades mediante microservicios.
* Descubrimiento automático utilizando Eureka.
* Centralización del acceso mediante API Gateway.
* Comunicación REST entre servicios utilizando Feign Client.
* Configuración desacoplada mediante archivos YAML.
* Documentación automática con Swagger.
* Validaciones concentradas en la capa Service.
* Pruebas unitarias sobre la lógica de negocio utilizando JUnit y Mockito.
* Persistencia implementada mediante Spring Data JPA.

Este diseño permite que cada componente evolucione de manera independiente, reduciendo el acoplamiento entre módulos y facilitando futuras ampliaciones del sistema.
