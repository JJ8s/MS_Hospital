package com.hospital.ms_recetas.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con datos de la receta emitida")
public class RecetaResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "1")
    private Long pacienteId;

    @Schema(example = "1")
    private Long productoId;

    @Schema(example = "2")
    private Integer cantidad;

    @Schema(example = "Tomar 1 comprimido cada 8 horas por 5 dias.")
    private String indicaciones;

    @Schema(example = "Dr. Matias Rodriguez")
    private String doctorResponsable;

    @Schema(example = "2026-06-29T22:30:00")
    private LocalDateTime fechaEmision;
}
