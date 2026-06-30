package com.hospital.ms_facturacion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de la receta es obligatorio")
    @Column(nullable = false)
    private Long recetaId;

    @NotNull(message = "El ID del paciente es obligatorio")
    @Column(nullable = false)
    private Long pacienteId;

    @NotNull(message = "El costo de servicio es obligatorio")
    @PositiveOrZero(message = "El costo de servicio debe ser mayor o igual a cero")
    @Column(nullable = false)
    private Double costoServicio;

    
    @PositiveOrZero(message = "El monto total debe ser mayor o igual a cero")
    @Column(nullable = false)
    private Double montoTotal;

    @Pattern(regexp = "PENDIENTE|PAGADA|ANULADA", message = "El estado debe ser PENDIENTE, PAGADA o ANULADA")
    @Column(nullable = false, length = 20)
    private String estado; 

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @PrePersist
    public void prePersist() {
        this.fechaEmision = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
        
    }
}
