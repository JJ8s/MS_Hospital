package com.hospital.ms_inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Cantidad usada para aumentar o descontar stock")
public class AjusteStockRequestDTO {

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Schema(example = "5")
    private Integer cantidad;
}
