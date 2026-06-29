package com.hospital.ms_citas.repository;

import com.hospital.ms_citas.model.EstadoCita;
import com.hospital.ms_citas.model.Model_citas;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository_citas extends JpaRepository<Model_citas, Long> {
    List<Model_citas> findByPacienteId(Long pacienteId);
    List<Model_citas> findByMedicoId(Long medicoId);
    List<Model_citas> findByEstado(EstadoCita estado);
    List<Model_citas> findByMedicoIdAndFechaAndHoraAndEstadoNot(Long medicoId, LocalDate fecha, LocalTime hora, EstadoCita estado);
}