package hospital.ms_recetas.service;

import hospital.ms_recetas.client.ProductoClient;
import hospital.ms_recetas.model.Receta;
import hospital.ms_recetas.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private ProductoClient productoClient;

    @Transactional(readOnly = true)
    public List<Receta> listarTodas() {
        return recetaRepository.findAll();
    }

   
    @Transactional
    public Receta guardar(Receta receta) {
        validarIntegridadReceta(receta);

        try {
            
            productoClient.reducirStock(receta.getProductoId(), receta.getCantidad());

           
            receta.setFechaEmision(LocalDateTime.now());
            return recetaRepository.save(receta);
            
        } catch (Exception e) {
            // Manejo adecuado de errores remotos (IE 2.4.1)
            throw new RuntimeException("Fallo en Interoperabilidad: No se pudo emitir la receta. " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Receta obtenerPorId(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error Clínico: La receta #" + id + " no existe en los registros."));
    }

    @Transactional(readOnly = true)
    public List<Receta> obtenerPorPaciente(Long pacienteId) {
        List<Receta> recetas = recetaRepository.findByPacienteId(pacienteId);
        if (recetas.isEmpty()) {
            throw new RuntimeException("Consulta: El paciente #" + pacienteId + " no tiene historial de recetas.");
        }
        return recetas;
    }

    @Transactional
    public void eliminar(Long id) {
        Receta receta = obtenerPorId(id);
        
        // Al anular una receta, se debe reponer el stock al inventario
        productoClient.reponerStock(receta.getProductoId(), receta.getCantidad());

        recetaRepository.deleteById(id);
    }

    @Transactional
    public Receta actualizarIndicaciones(Long id, Receta detalles) {
        Receta recetaExistente = obtenerPorId(id);

        // No se permite cambiar el medicamento ni la cantidad una vez emitida
        // Solo se permiten cambios en las instrucciones de administración por seguridad clínica
        recetaExistente.setIndicaciones(detalles.getIndicaciones());
        recetaExistente.setDoctorResponsable(detalles.getDoctorResponsable());
        
        return recetaRepository.save(recetaExistente);
    }

    // --- MÉTODOS PRIVADOS DE SOPORTE ---

    private void validarIntegridadReceta(Receta receta) {
        if (receta.getPacienteId() == null) {
            throw new IllegalArgumentException("Error de Validación: El ID del paciente es obligatorio.");
        }
        if (receta.getCantidad() == null || receta.getCantidad() <= 0) {
            throw new IllegalArgumentException("Error de Validación: La cantidad recetada debe ser mayor a cero.");
        }
        if (receta.getProductoId() == null) {
            throw new IllegalArgumentException("Error de Validación: Debe especificar un producto del inventario.");
        }
    }
}