package hospital.ms_facturacion.repository;

import hospital.ms_facturacion.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    // 1. Buscar factura por receta 
    Optional<Factura> findByRecetaId(Long recetaId);

    // 2. Buscar todas las facturas de un paciente 
    List<Factura> findByPacienteId(Long pacienteId);

    // 3. Buscar por estado (PAGADA / PENDIENTE)
    List<Factura> findByEstado(String estado);

    // 4. Buscar facturas pendientes de un paciente específico
    List<Factura> findByPacienteIdAndEstado(Long pacienteId, String estado);
}