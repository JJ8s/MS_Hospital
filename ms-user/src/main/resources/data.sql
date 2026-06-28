-- Usamos IGNORE para evitar el error de duplicidad
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_USER');


INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_MEDICO');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_PACIENTE');

-- Usamos INSERT IGNORE para que no falle si el rol ya existe
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_USER');

-- Lo mismo para el usuario inicial
INSERT IGNORE INTO users (id, nombre, apellido, telefono, contrasena, auto_email, active) 
VALUES (1, 'Anderson', 'Ovando', '123456789', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnUeWp.7Wf.T3l.36vW0V0UuY5C.', 'anderson.ovando@hospital.com', 1);

-- Para la relación, puedes usar un REPLACE o asegurar que no se duplique
REPLACE INTO user_roles (user_id, role_id) VALUES (1, 1);