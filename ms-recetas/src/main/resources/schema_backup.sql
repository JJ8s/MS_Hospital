-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS db_hospital_recetas;
USE db_hospital_recetas;

DROP TABLE IF EXISTS recetas;

CREATE TABLE recetas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    paciente_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    doctor_responsable VARCHAR(100),
    indicaciones TEXT,
    fecha_emision DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
