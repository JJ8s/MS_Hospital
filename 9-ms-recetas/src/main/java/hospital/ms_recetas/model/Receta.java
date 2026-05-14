package hospital.ms_recetas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recetas")
@Data
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId; // Referencia lógica al ms-pacientes

    @NotNull(message = "El ID del producto (medicamento) es obligatorio")
    private Long productoId; // Referencia lógica al ms-inventario

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "Las indicaciones médicas son obligatorias")
    @Size(min = 10, max = 500, message = "Las indicaciones deben tener entre 10 y 500 caracteres")
    private String indicaciones;

    @NotBlank(message = "El nombre del doctor es obligatorio")
    @Pattern(regexp = "^[a-zA-Z\\s.]+$", message = "El nombre del doctor solo debe contener letras")
    private String doctorResponsable;
    
    @Column(updatable = false)
    private LocalDateTime fechaEmision;

    @PrePersist
    public void prePersist() {
        this.fechaEmision = LocalDateTime.now();
    }
}
