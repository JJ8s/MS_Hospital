package com.hospital.ms_citas.dto.request;

import com.hospital.ms_citas.model.EstadoCita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para agendar o actualizar una cita médica")
public class CitaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Schema(description = "ID del paciente existente en ms-pacientes", example = "1")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    @Schema(description = "ID del médico existente en ms-medicos", example = "7")
    private Long medicoId;

    @NotNull(message = "La fecha no puede estar vacía")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro")
    @Schema(description = "Fecha de la cita", example = "2026-07-01")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Schema(description = "Hora de la cita", example = "10:30:00")
    private LocalTime hora;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado actual de la cita", example = "PENDIENTE")
    private EstadoCita estado;

    @Schema(description = "Observaciones adicionales de la cita", example = "Control general")
    private String observaciones;
}
