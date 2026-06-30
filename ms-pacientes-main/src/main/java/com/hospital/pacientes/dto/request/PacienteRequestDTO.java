package com.hospital.pacientes.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear o actualizar un paciente")
public class PacienteRequestDTO {

    @NotBlank(message = "El RUT es obligatorio")
    @Schema(description = "RUT del paciente", example = "98765432-1")
    private String rut;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del paciente", example = "Carlos")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Schema(description = "Apellido del paciente", example = "Rojas")
    private String apellido;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 150, message = "La edad máxima debe ser coherente a la realidad")
    @Schema(description = "Edad del paciente", example = "30")
    private Integer edad;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Schema(description = "Teléfono de contacto", example = "+56911112222")
    private String telefono;

    @NotBlank(message = "La previsión no puede estar vacía")
    @Schema(description = "Previsión del paciente", example = "FONASA")
    private String prevision;
}
