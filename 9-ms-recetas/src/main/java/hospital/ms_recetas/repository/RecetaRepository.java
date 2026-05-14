package hospital.ms_recetas.repository;

import hospital.ms_recetas.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    // 1. Buscar todas las recetas de un paciente específico
    List<Receta> findByPacienteId(Long pacienteId);

    // 2. Buscar recetas emitidas por un doctor
    List<Receta> findByDoctorResponsableContainingIgnoreCase(String doctor);

    // 3. Buscar quién tiene recetado un producto específico
    List<Receta> findByProductoId(Long productoId);
    
    // 4. Buscar recetas recientes 
    List<Receta> findTop10ByOrderByFechaEmisionDesc();
}
