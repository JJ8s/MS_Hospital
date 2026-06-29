package com.hospital.pacientes.dto.response;

import lombok.Data;

@Data
public class PacienteResponseDTO {

    private Long id;

    private String rut;

    private String nombre;

    private String apellido;

    private Integer edad;

    private String telefono;

    private String prevision;
}
