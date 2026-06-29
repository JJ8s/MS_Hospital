package com.hospital.ms_medicos.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MedicoResponseDTO {

    private Long id;

    private String rut;

    private String nombre;

    private String apellido;

    private String especialidad;

    private String sector;

    private String email;

    private String telefono;

    private Double sueldo;

    private LocalDate fecha_contratacion;

    private Integer annios_edad;

    private Integer annios_experiencia;
}