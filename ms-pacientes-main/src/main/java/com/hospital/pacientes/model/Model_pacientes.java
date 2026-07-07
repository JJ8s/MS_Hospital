package com.hospital.pacientes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "pacientes")
@Data
public class Model_pacientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUT es obligatorio")
    @Column(unique = true, nullable = false)
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;

    @NotNull(message = "La edad del paciente es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 150, message = "La edad máxima debe ser coherente a la realidad")
    @Column(nullable = false)
    private Integer edad;

    @NotBlank(message = "El número de teléfono del paciente es obligatorio")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "La previsión es obligatoria")
    @Column(nullable = false)
    private String prevision;
}
