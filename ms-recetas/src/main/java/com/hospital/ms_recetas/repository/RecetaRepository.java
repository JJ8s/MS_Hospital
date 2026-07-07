package com.hospital.ms_recetas.repository;

import com.hospital.ms_recetas.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    
    List<Receta> findByPacienteId(Long pacienteId);

    
    List<Receta> findByDoctorResponsableContainingIgnoreCase(String doctor);

    
    List<Receta> findByProductoId(Long productoId);
    
    
    List<Receta> findTop10ByOrderByFechaEmisionDesc();
}
