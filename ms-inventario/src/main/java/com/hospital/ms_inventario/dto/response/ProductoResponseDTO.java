package com.hospital.ms_inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con datos del producto registrado")
public class ProductoResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Paracetamol 500mg")
    private String nombre;

    @Schema(example = "Analgesico y antipiretico basico")
    private String descripcion;

    @Schema(example = "1500")
    private Double precio;

    @Schema(example = "99")
    private Integer stock;

    @Schema(example = "LT-2026-A1")
    private String lote;

    @Schema(example = "2027-12-31")
    private LocalDate fechaVencimiento;
}
