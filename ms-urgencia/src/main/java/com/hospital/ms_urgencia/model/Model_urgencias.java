package com.hospital.ms_urgencia.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "urgencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model_urgencias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del paciente es obligatorio")
    @Column(nullable = false)
    private Long pacienteId;

    @NotNull(message = "El nivel de triage es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelTriage nivelTriage;

    @NotBlank(message = "El motivo de ingreso es obligatorio")
    @Column(nullable = false)
    private String motivoIngreso;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = false)
    private String estadoActual;

    @PrePersist
    public void prePersist() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }
        if (estadoActual == null || estadoActual.isBlank()) {
            estadoActual = "EN_ESPERA";
        }
    }
}
