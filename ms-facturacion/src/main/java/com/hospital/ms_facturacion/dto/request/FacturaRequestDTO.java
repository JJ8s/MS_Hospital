package com.hospital.ms_facturacion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para generar una factura")
public class FacturaRequestDTO {

    @NotNull(message = "El ID de la receta es obligatorio")
    @Schema(example = "1")
    private Long recetaId;

    @NotNull(message = "El costo de servicio es obligatorio")
    @PositiveOrZero(message = "El costo de servicio debe ser mayor o igual a cero")
    @Schema(example = "500")
    private Double costoServicio;
}
