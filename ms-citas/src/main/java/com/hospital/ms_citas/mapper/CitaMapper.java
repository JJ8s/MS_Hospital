package com.hospital.ms_citas.mapper;

import com.hospital.ms_citas.dto.request.CitaRequestDTO;
import com.hospital.ms_citas.dto.response.CitaResponseDTO;
import com.hospital.ms_citas.model.Model_citas;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper {
   public Model_citas toEntity(CitaRequestDTO dto) {
      Model_citas cita = new Model_citas();
      cita.setPacienteId(dto.getPacienteId());
      cita.setMedicoId(dto.getMedicoId());
      cita.setFecha(dto.getFecha());
      cita.setHora(dto.getHora());
      cita.setEstado(dto.getEstado());
      cita.setObservaciones(dto.getObservaciones());
      return cita;
   }

   public CitaResponseDTO toResponse(Model_citas cita) {
      CitaResponseDTO dto = new CitaResponseDTO();
      dto.setId(cita.getId());
      dto.setPacienteId(cita.getPacienteId());
      dto.setMedicoId(cita.getMedicoId());
      dto.setFecha(cita.getFecha());
      dto.setHora(cita.getHora());
      dto.setEstado(cita.getEstado());
      dto.setObservaciones(cita.getObservaciones());
      return dto;
   }

   public void updateEntity(Model_citas cita, CitaRequestDTO dto) {
      cita.setPacienteId(dto.getPacienteId());
      cita.setMedicoId(dto.getMedicoId());
      cita.setFecha(dto.getFecha());
      cita.setHora(dto.getHora());
      cita.setEstado(dto.getEstado());
      cita.setObservaciones(dto.getObservaciones());
   }
}
