package com.hospital.ms_urgencia.dto.request;

import com.hospital.ms_urgencia.model.NivelTriage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrgenciaRequestDTO {

    @Schema(example = "1", description = "ID del paciente registrado en ms-pacientes")
    @NotNull(message = "El id del paciente es obligatorio")
    private Long pacienteId;

    @Schema(example = "AMARILLO", description = "Nivel de triage: ROJO, NARANJA, AMARILLO, VERDE o AZUL")
    @NotNull(message = "El nivel de triage es obligatorio")
    private NivelTriage nivelTriage;

    @Schema(example = "Dolor abdominal intenso", description = "Motivo de ingreso del paciente a urgencias")
    @NotBlank(message = "El motivo de ingreso es obligatorio")
    private String motivoIngreso;
}
