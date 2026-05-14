package hospital.ms_recetas.service;

import hospital.ms_recetas.client.ProductoClient;
import hospital.ms_recetas.model.Receta;
import hospital.ms_recetas.repository.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private ProductoClient productoClient;

    // Listar todas
    public List<Receta> listarTodas() {
        return recetaRepository.findAll();
    }

    // Guardar y Descontar
    @Transactional
    public Receta guardar(Receta receta) {
        try {
            productoClient.reducirStock(receta.getProductoId(), 1); 
            
            return recetaRepository.save(receta);
            
        } catch (Exception e) {
            throw new RuntimeException("No se pudo emitir la receta: " + e.getMessage());
        }
    }

    public Receta obtenerPorId(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error Clínico: La receta #" + id + " no existe."));
    }

    // Buscar por paciente 
    public List<Receta> obtenerPorPaciente(Long pacienteId) {
        return recetaRepository.findByPacienteId(pacienteId);
    }

    @Transactional
    public void eliminar(Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: La receta no existe."));
        productoClient.reponerStock(receta.getProductoId(), receta.getCantidad());

        recetaRepository.deleteById(id);
    }

    public Receta actualizarIndicaciones(Long id, Receta detalles) {
    
    Receta receta = recetaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + id));

    
    receta.setIndicaciones(detalles.getIndicaciones());
    receta.setDoctorResponsable(detalles.getDoctorResponsable());
    
    return recetaRepository.save(receta);
    }
}