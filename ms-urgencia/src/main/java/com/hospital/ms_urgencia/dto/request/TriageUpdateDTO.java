package com.hospital.ms_urgencia.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriageUpdateDTO {

    @Schema(example = "ROJO", description = "Nuevo nivel de triage")
    @NotBlank(message = "El nivel de triage es obligatorio")
    private String nivel;
}
