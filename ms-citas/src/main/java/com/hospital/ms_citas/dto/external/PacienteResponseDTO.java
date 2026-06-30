package com.hospital.ms_citas.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String telefono;
    private String prevision;
}
