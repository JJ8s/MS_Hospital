-- Crear la bd si no existe
CREATE DATABASE IF NOT EXISTS db_hospital_facturacion;
USE db_hospital_facturacion;

DROP TABLE IF EXISTS facturas;

CREATE TABLE facturas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receta_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    costo_servicio DOUBLE NOT NULL,
    monto_total DOUBLE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_emision DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
