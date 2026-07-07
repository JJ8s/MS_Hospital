package com.hospital.ms_facturacion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con datos de la factura")
public class FacturaResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "1")
    private Long recetaId;

    @Schema(example = "1")
    private Long pacienteId;

    @Schema(example = "500")
    private Double costoServicio;

    @Schema(example = "2000")
    private Double montoTotal;

    @Schema(example = "PENDIENTE")
    private String estado;

    @Schema(example = "2026-06-29T22:30:00")
    private LocalDateTime fechaEmision;
}
