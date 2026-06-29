package com.hospital.pacientes.mapper;

import org.springframework.stereotype.Component;

import com.hospital.pacientes.dto.request.PacienteRequestDTO;
import com.hospital.pacientes.dto.response.PacienteResponseDTO;
import com.hospital.pacientes.model.Model_pacientes;

@Component
public class PacienteMapper {

    public Model_pacientes toEntity(PacienteRequestDTO dto) {
        Model_pacientes paciente = new Model_pacientes();
        paciente.setRut(dto.getRut());
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setEdad(dto.getEdad());
        paciente.setTelefono(dto.getTelefono());
        paciente.setPrevision(dto.getPrevision());
        return paciente;
    }

    public PacienteResponseDTO toResponse(Model_pacientes paciente) {
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setRut(paciente.getRut());
        dto.setNombre(paciente.getNombre());
        dto.setApellido(paciente.getApellido());
        dto.setEdad(paciente.getEdad());
        dto.setTelefono(paciente.getTelefono());
        dto.setPrevision(paciente.getPrevision());
        return dto;
    }

    public void updateEntity(Model_pacientes paciente, PacienteRequestDTO dto) {
        paciente.setRut(dto.getRut());
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setEdad(dto.getEdad());
        paciente.setTelefono(dto.getTelefono());
        paciente.setPrevision(dto.getPrevision());
    }
}
