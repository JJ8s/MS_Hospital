package com.hospital.ms_urgencia.repository;

import com.hospital.ms_urgencia.model.Model_urgencias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Repository_urgencias extends JpaRepository<Model_urgencias, Long> {

    List<Model_urgencias> findByPacienteId(Long pacienteId);
}
