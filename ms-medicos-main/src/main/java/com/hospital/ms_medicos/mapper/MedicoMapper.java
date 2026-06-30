package com.hospital.ms_medicos.mapper;

import org.springframework.stereotype.Component;

import com.hospital.ms_medicos.dto.request.MedicoRequestDTO;
import com.hospital.ms_medicos.dto.response.MedicoResponseDTO;
import com.hospital.ms_medicos.model.Model_medicos;

@Component
public class MedicoMapper {

    public Model_medicos toEntity(MedicoRequestDTO dto) {

        Model_medicos medico = new Model_medicos();

        medico.setRut(dto.getRut());
        medico.setNombre(dto.getNombre());
        medico.setApellido(dto.getApellido());
        medico.setEspecialidad(dto.getEspecialidad());
        medico.setSector(dto.getSector());
        medico.setEmail(dto.getEmail());
        medico.setTelefono(dto.getTelefono());
        medico.setSueldo(dto.getSueldo());
        medico.setFecha_contratacion(dto.getFecha_contratacion());
        medico.setAnnios_edad(dto.getAnnios_edad());
        medico.setAnnios_experiencia(dto.getAnnios_experiencia());

        return medico;
    }

    public MedicoResponseDTO toResponse(Model_medicos medico) {

        MedicoResponseDTO dto = new MedicoResponseDTO();

        dto.setId(medico.getId());
        dto.setRut(medico.getRut());
        dto.setNombre(medico.getNombre());
        dto.setApellido(medico.getApellido());
        dto.setEspecialidad(medico.getEspecialidad());
        dto.setSector(medico.getSector());
        dto.setEmail(medico.getEmail());
        dto.setTelefono(medico.getTelefono());
        dto.setSueldo(medico.getSueldo());
        dto.setFecha_contratacion(medico.getFecha_contratacion());
        dto.setAnnios_edad(medico.getAnnios_edad());
        dto.setAnnios_experiencia(medico.getAnnios_experiencia());

        return dto;
    }

    public void updateEntity(Model_medicos medico, MedicoRequestDTO dto) {

        medico.setRut(dto.getRut());
        medico.setNombre(dto.getNombre());
        medico.setApellido(dto.getApellido());
        medico.setEspecialidad(dto.getEspecialidad());
        medico.setSector(dto.getSector());
        medico.setEmail(dto.getEmail());
        medico.setTelefono(dto.getTelefono());
        medico.setSueldo(dto.getSueldo());
        medico.setFecha_contratacion(dto.getFecha_contratacion());
        medico.setAnnios_edad(dto.getAnnios_edad());
        medico.setAnnios_experiencia(dto.getAnnios_experiencia());
    }
}