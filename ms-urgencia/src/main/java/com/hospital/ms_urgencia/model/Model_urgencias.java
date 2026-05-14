package com.hospital.ms_urgencia.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "urgencia")
@Data
public class Model_urgencias {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message ="El id del paciente es obligatorio")
    private Long pacienteId;

    @NotBlank(message= "El nivel de triage es obligatorio")
    @Enumerated(EnumType.STRING)
    private NivelTriage nivelTriage;

    @NotBlank(message = "El motivo de ingreso es obligatorio")
    private String motivoIngreso;

    private LocalDateTime fechaIngreso = LocalDateTime.now();
    private String estadoActual = "EN_ESPERA";
}
