package com.hospital.pacientes.repository;

import com.hospital.pacientes.model.Model_pacientes;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository_pacientes extends JpaRepository<Model_pacientes, Long> {
    Optional<Model_pacientes> findByRut(String rut);
    List<Model_pacientes> findByPrevision(String prevision);
}
