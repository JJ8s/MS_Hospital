-- 1. Insertar Roles de forma segura
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_MEDICO');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_PACIENTE');

-- 2. Insertar Anderson
INSERT IGNORE INTO users (id, nombre, apellido, telefono, contrasena, autoemail, active) 
VALUES (1, 'Anderson', 'Ovando', '123456789', '$2a$10$e0MbgS6vB0.wN.K8XFpMhu35E/8uP5U66.d6b8P.u6mEAnT/V0F22', 'anderson.ovando@hospital.com', 1);

-- 3. Vincular tabla intermedia
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (1, 1);