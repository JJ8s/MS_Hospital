# Sistema de Gestión Hospitalaria - Arquitectura de Microservicios

Este rama del repositorio contiene la solución backend para la gestión hospitalaria basada en una arquitectura de microservicios utilizando **Spring Boot**, **Spring Cloud** y **Java 17**. El sistema automatiza el flujo clínico desde la emisión de recetas médicas hasta el control de stock en farmacia y la generación automática de facturas.

## 👥 Integrantes
* **Matias Currin** - Desarrollador Backend (Microservicios de Inventario, Recetas y Facturación)

---

## 🏗️ Arquitectura del Sistema

El ecosistema está diseñado bajo un patrón de microservicios descentralizados, comunicados de forma síncrona y registrados en un servidor de descubrimiento.

### Componentes de Infraestructura:
* **Eureka Server (Puerto 8761):** Servidor de descubrimiento que centraliza el registro de todos los microservicios activos.
* **API Gateway (Puerto 8080):** Puerta de entrada única del sistema que enruta las peticiones HTTP externas hacia los microservicios correspondientes de manera segura.

### Microservicios de Negocio:
1. **ms-inventario:** Se encarga del ciclo de vida de los productos e insumos médicos (CRUD, control de stock físico, alertas de vencimiento y lote).
2. **ms-recetas:** Permite la emisión de recetas médicas asociadas a un paciente y a un doctor responsable. Coordina la validación de insumos con el inventario.
3. **ms-facturacion:** Gestiona los cobros del hospital, calculando dinámicamente los montos totales sumando el costo del servicio médico y el precio de los medicamentos entregados.

---

## 🛠️ Decisiones de Diseño Técnico

* **Comunicación Síncrona (Feign Client):** Se utiliza Declarative REST Clients para conectar `ms-recetas` con `ms-inventario`, y `ms-facturacion` con los servicios base, logrando un flujo de datos inmediato y consistente.
* **Transaccionalidad Segura (`@Transactional`):** Para mitigar la inconsistencia eventual entre bases de datos, las operaciones críticas primero aseguran la persistencia local antes de gatillar peticiones externas.
* **Lógica de Compensación:** Si una receta es eliminada, el sistema dispara automáticamente un evento `PUT` hacia el inventario para reponer las unidades y evitar mermas fantasma en el stock digital.
* **Modelo de Dominio Compartido:** En esta fase de MVP se priorizó un modelo de dominio directo para acelerar el desarrollo y mantener un acoplamiento controlado, resguardando la entrada de datos con **Bean Validation** (`@NotNull`, `@Min`).

---

## 🚀 Guía de Pruebas en Postman (Flujo Integrado)

Para comprobar el funcionamiento real del sistema a través del API Gateway, ejecute las siguientes peticiones HTTP en el orden indicado:

### 1. Registrar Producto Inicial (`ms-inventario`)
* **Método:** `POST`
* **Endpoint:** `http://localhost:8080/api/productos`
* **JSON de entrada:**
```json
{
    "nombre": "Amoxicilina 500mg",
    "descripcion": "Antibiótico de amplio espectro",
    "precio": 12500.0,
    "stock": 50,
    "lote": "L-2026-X",
    "fechaVencimiento": "2026-12-20"
}

{
    "pacienteId": 101,
    "productoId": 2,
    "cantidad": 2,
    "doctorResponsable": "Dr. Cristiano Ronaldo",
    "indicaciones": "Tomar 1 comprimido cada 8 horas por 5 días."
}

{
    "pacienteId": 101,
    "recetaId": 2,
    "costoServicio": 3500.0
}
