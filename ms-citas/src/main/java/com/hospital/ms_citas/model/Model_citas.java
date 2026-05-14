package com.hospital.ms_citas.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name="citas")
@Data
public class Model_citas {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El id del medico es obligatorio")
    private Long medicoId;
    
    @NotNull(message = "la fecha no puede estar vacia")
    @FutureOrPresent(message = "la fecha debe ser hoy o en el futuro")
    private LocalDate fecha;

    @NotNull(message = "la hora es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "EL estado es obligatorio (Pendiente, cancelado, realizado)")
    @Enumerated(EnumType.STRING)
    private EstadoCita estado;
    
    private String observaciones;

}
