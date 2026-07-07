package com.hospital.ms_facturacion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Datos permitidos para actualizar una factura pendiente")
public class FacturaUpdateDTO {

    @NotNull(message = "El costo de servicio es obligatorio")
    @PositiveOrZero(message = "El costo de servicio debe ser mayor o igual a cero")
    @Schema(example = "750")
    private Double costoServicio;
}
