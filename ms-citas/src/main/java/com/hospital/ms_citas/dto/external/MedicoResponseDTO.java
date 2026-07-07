package com.hospital.ms_citas.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
