-- 1.Crear la bd
CREATE DATABASE IF NOT EXISTS db_hospital_inventario;
USE db_hospital_inventario;

-- 2. Eliminar tablas existentes
DROP TABLE IF EXISTS productos;

-- 3. Definición explícita de la tabla 
CREATE TABLE productos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255) NOT NULL, 
    lote VARCHAR(50) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

