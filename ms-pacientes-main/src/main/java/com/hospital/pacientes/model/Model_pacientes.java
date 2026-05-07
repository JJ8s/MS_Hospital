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
@Table(name="pacientes")
@Data
public class Model_pacientes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUT es obligatorio")
    @Column(unique=true)
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotNull(message= "La edad del pacientes es obligatoria")
    @Min(value=0, message ="la edad no puede ser negativa")
    @Max(value=150,message ="La edad maxima debe ser coherente a la realidad" )
    private int edad;

    @NotBlank(message= "El numero de telefono del pacientes es obligatorio")
    private String telefono;

    @NotBlank(message = "El prevision es obligatorio")
    private String prevision;//si es fonasa o isapre etcc

}
