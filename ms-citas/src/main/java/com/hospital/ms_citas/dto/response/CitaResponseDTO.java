package com.hospital.ms_citas.dto.response;

import com.hospital.ms_citas.model.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaResponseDTO {

    private Long id;

    private Long pacienteId;

    private Long medicoId;

    private LocalDate fecha;

    private LocalTime hora;

    private EstadoCita estado;

    private String observaciones;

}