package com.hospital.ms_medicos.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicoRequestDTO {

    @NotBlank(message = "El rut del medico es obligatorio")
    private String rut;

    @NotBlank(message = "El nombre del medico no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido del medico no puede estar vacío")
    private String apellido;

    @NotBlank(message = "La especialidad no puede estar vacía")
    private String especialidad;

    @NotBlank(message = "El sector no puede estar vacío")
    private String sector;

    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;

    @NotNull(message = "El sueldo es obligatorio")
    private Double sueldo;

    @NotNull(message = "La fecha de contratación es obligatoria")
    private LocalDate fecha_contratacion;

    @NotNull(message = "La edad es obligatoria")
    private Integer annios_edad;

    @NotNull(message = "Los años de experiencia son obligatorios")
    private Integer annios_experiencia;
}