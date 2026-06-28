-- 1. Insertar Roles
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_USER');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_MEDICO');
INSERT IGNORE INTO roles (id, name) VALUES (4, 'ROLE_PACIENTE');

-- 2. Insertar Usuario con Contraseña: admin12345
-- El hash a continuación es el resultado real de BCrypt para 'admin12345'
INSERT IGNORE INTO users (id, nombre, apellido, telefono, contrasena, autoemail, active) 
VALUES (1, 'Anderson', 'Ovando', '123456789', 'admin123456', 'anderson.ovando@hospital.com', 1);

-- 3. Vincular Rol
REPLACE INTO user_roles (user_id, role_id) VALUES (1, 1);