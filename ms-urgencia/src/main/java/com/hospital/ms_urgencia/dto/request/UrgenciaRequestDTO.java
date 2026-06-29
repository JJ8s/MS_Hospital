package com.hospital.ms_urgencia.dto.request;

import com.hospital.ms_urgencia.model.NivelTriage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrgenciaRequestDTO {

    @NotNull(message = "El id del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El nivel de triage es obligatorio")
    private NivelTriage nivelTriage;

    @NotBlank(message = "El motivo de ingreso es obligatorio")
    private String motivoIngreso;
}