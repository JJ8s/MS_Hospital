-- Datos de prueba
INSERT INTO facturas (receta_id, paciente_id, costo_servicio, monto_total, estado, fecha_emision)
VALUES (1, 1, 500.0, 2000, 'PENDIENTE', NOW());

INSERT INTO facturas (receta_id, paciente_id, costo_servicio, monto_total, estado, fecha_emision)
VALUES (2, 2, 1000.0, 3500, 'PAGADA', NOW());