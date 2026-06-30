-- Un dato de prueba inicial (Ejemplo: Paciente 1, Producto 1 del inventario)
INSERT INTO recetas (paciente_id, producto_id, cantidad, indicaciones, doctor_responsable, fecha_emision)
VALUES (1, 1, 2, 'Tomar 1 comprimido cada 8 horas por 5 días.', 'Dr. Matias Rodriguez', NOW());