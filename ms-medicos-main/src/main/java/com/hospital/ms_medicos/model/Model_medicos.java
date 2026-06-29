package com.hospital.ms_medicos.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name= "medicos")
@Data
public class Model_medicos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El rut del medico es obligatorio")
    @Column(unique=true)
    private String rut;

    @NotBlank(message = "El nombre de medico no puede estar vaico")
    private String nombre;
    
    @NotBlank(message= "El apellido del medico no puede estar vacio")
    private String apellido;

    @NotBlank(message= "La especialidad del medico no puede estar vacio")
    private String especialidad;

    @NotBlank(message= "El sector donde atiende no puede estar vacio")
    private String sector;

    @NotBlank(message= "El email no puede estar vacio")
    private String email;

    @NotBlank(message= "El numero de telefono no puede estar vacio")
    private String telefono;

    @NotNull(message ="El sueldo del medico no puede estar vacio")
    private Double sueldo;

    @NotNull(message ="La fecha de contratacion del medico no puede estar vacia")
    private LocalDate fecha_contratacion;

    @NotNull(message= "La edad del medico no puede estar vacio")
    private Integer annios_edad;

    @NotNull(message = "Annios de experiencia del medico no puede estar vacio")
    private Integer annios_experiencia;

}
