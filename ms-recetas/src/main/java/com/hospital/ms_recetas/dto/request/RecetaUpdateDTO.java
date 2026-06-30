package com.hospital.ms_recetas.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos permitidos para actualizar una receta")
public class RecetaUpdateDTO {

    @NotBlank(message = "Las indicaciones medicas son obligatorias")
    @Size(min = 10, max = 500, message = "Las indicaciones deben tener entre 10 y 500 caracteres")
    @Schema(example = "Tomar con abundante agua despues de cada comida.")
    private String indicaciones;

    @NotBlank(message = "El nombre del doctor es obligatorio")
    @Pattern(regexp = "^[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s.]+$", message = "El nombre del doctor solo debe contener letras")
    @Schema(example = "Dra. Ana Lopez")
    private String doctorResponsable;
}
