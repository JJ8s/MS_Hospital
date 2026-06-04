package com.hospital.ms_medicos.repository;
import com.hospital.ms_medicos.model.Model_medicos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository_medicos extends JpaRepository<Model_medicos, Long >{
    Optional<Model_medicos> findByRut(String rut);
    List<Model_medicos> findByEspecialidad(String especialidad);
}
