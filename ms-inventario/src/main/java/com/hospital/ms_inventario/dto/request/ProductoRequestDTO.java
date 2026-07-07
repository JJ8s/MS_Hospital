package com.hospital.ms_inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Datos requeridos para crear o actualizar un producto del inventario")
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(example = "Paracetamol 500mg")
    private String nombre;

    @NotBlank(message = "La descripcion es obligatoria")
    @Schema(example = "Analgesico y antipiretico basico")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a cero")
    @Schema(example = "1500")
    private Double precio;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(example = "99")
    private Integer stock;

    @NotBlank(message = "El codigo de lote es obligatorio")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El lote solo debe contener letras, numeros y guiones")
    @Schema(example = "LT-2026-A1")
    private String lote;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser una fecha futura")
    @Schema(example = "2027-12-31")
    private LocalDate fechaVencimiento;
}
