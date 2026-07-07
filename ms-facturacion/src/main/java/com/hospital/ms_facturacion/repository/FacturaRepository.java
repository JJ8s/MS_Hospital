package com.hospital.ms_facturacion.repository;

import com.hospital.ms_facturacion.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    
    Optional<Factura> findByRecetaId(Long recetaId);

    
    List<Factura> findByPacienteId(Long pacienteId);

    
    List<Factura> findByEstado(String estado);

    
    List<Factura> findByPacienteIdAndEstado(Long pacienteId, String estado);
}