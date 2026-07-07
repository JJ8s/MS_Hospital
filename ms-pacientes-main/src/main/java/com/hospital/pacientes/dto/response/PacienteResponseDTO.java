package com.hospital.pacientes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta con datos públicos del paciente")
public class PacienteResponseDTO {

    @Schema(description = "ID interno del paciente", example = "1")
    private Long id;

    @Schema(description = "RUT del paciente", example = "98765432-1")
    private String rut;

    @Schema(description = "Nombre del paciente", example = "Carlos")
    private String nombre;

    @Schema(description = "Apellido del paciente", example = "Rojas")
    private String apellido;

    @Schema(description = "Edad del paciente", example = "30")
    private Integer edad;

    @Schema(description = "Teléfono de contacto", example = "+56911112222")
    private String telefono;

    @Schema(description = "Previsión del paciente", example = "FONASA")
    private String prevision;
}
