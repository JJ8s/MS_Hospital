package com.hospital.ms_urgencia.dto.response;

import com.hospital.ms_urgencia.model.NivelTriage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrgenciaResponseDTO {

    private Long id;
    private Long pacienteId;
    private NivelTriage nivelTriage;
    private String motivoIngreso;
    private LocalDateTime fechaIngreso;
    private String estadoActual;
}
