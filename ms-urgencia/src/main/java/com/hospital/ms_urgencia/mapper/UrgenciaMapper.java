package com.hospital.ms_urgencia.mapper;

import com.hospital.ms_urgencia.dto.request.UrgenciaRequestDTO;
import com.hospital.ms_urgencia.dto.response.UrgenciaResponseDTO;
import com.hospital.ms_urgencia.model.Model_urgencias;
import org.springframework.stereotype.Component;

@Component
public class UrgenciaMapper {

    public Model_urgencias toEntity(UrgenciaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Model_urgencias urgencia = new Model_urgencias();
        urgencia.setPacienteId(dto.getPacienteId());
        urgencia.setNivelTriage(dto.getNivelTriage());
        urgencia.setMotivoIngreso(dto.getMotivoIngreso());
        urgencia.setEstadoActual("EN_ESPERA");
        return urgencia;
    }

    public UrgenciaResponseDTO toResponse(Model_urgencias urgencia) {
        if (urgencia == null) {
            return null;
        }

        return new UrgenciaResponseDTO(
                urgencia.getId(),
                urgencia.getPacienteId(),
                urgencia.getNivelTriage(),
                urgencia.getMotivoIngreso(),
                urgencia.getFechaIngreso(),
                urgencia.getEstadoActual()
        );
    }
}
