package com.hospital.ms_notificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del destinatario es obligatorio")
    @Column(nullable = false)
    private Long destinatarioId;

    @NotBlank(message = "El tipo de destinatario es obligatorio")
    @Pattern(regexp = "PACIENTE|MEDICO|ADMIN", message = "El tipo debe ser PACIENTE, MEDICO o ADMIN")
    @Column(nullable = false, length = 20)
    private String tipoDestinatario;

    @NotBlank(message = "El canal es obligatorio")
    @Pattern(regexp = "EMAIL|SMS|APP", message = "El canal debe ser EMAIL, SMS o APP")
    @Column(nullable = false, length = 20)
    private String canal;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 3, max = 120, message = "El asunto debe tener entre 3 y 120 caracteres")
    @Column(nullable = false, length = 120)
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 5, max = 500, message = "El mensaje debe tener entre 5 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String mensaje;

    @Pattern(regexp = "PENDIENTE|ENVIADA|LEIDA|FALLIDA", message = "El estado debe ser PENDIENTE, ENVIADA, LEIDA o FALLIDA")
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaEnvio;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
    }
}
