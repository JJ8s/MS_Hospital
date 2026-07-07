package com.hospital.ms_recetas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para emitir una receta medica")
public class RecetaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Schema(example = "1")
    private Long pacienteId;

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(example = "1")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(example = "2")
    private Integer cantidad;

    @NotBlank(message = "Las indicaciones medicas son obligatorias")
    @Size(min = 10, max = 500, message = "Las indicaciones deben tener entre 10 y 500 caracteres")
    @Schema(example = "Tomar 1 comprimido cada 8 horas por 5 dias.")
    private String indicaciones;

    @NotBlank(message = "El nombre del doctor es obligatorio")
    @Pattern(regexp = "^[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s.]+$", message = "El nombre del doctor solo debe contener letras")
    @Schema(example = "Dr. Matias Rodriguez")
    private String doctorResponsable;
}
