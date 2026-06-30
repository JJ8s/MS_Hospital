package com.hospital.pacientes.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.pacientes.dto.request.PacienteRequestDTO;
import com.hospital.pacientes.dto.response.PacienteResponseDTO;
import com.hospital.pacientes.exception.DuplicadoException;
import com.hospital.pacientes.exception.ResourceNotFoundException;
import com.hospital.pacientes.mapper.PacienteMapper;
import com.hospital.pacientes.model.Model_pacientes;
import com.hospital.pacientes.repository.Repository_pacientes;

@Service
public class Service_pacientes {

    private final Repository_pacientes repository;
    private final PacienteMapper mapper;

    public Service_pacientes(Repository_pacientes repository, PacienteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /* Obtener todos */
    public List<PacienteResponseDTO> obtenerTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /* Obtener por ID */
    public PacienteResponseDTO obtenerPorId(Long id) {
        Model_pacientes paciente = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Paciente no encontrado con ID " + id));
        return mapper.toResponse(paciente);
    }

    /* Guardar */
    public PacienteResponseDTO guardar(PacienteRequestDTO dto) {
        if (repository.findByRut(dto.getRut()).isPresent()) {
            throw new DuplicadoException("Ya existe un paciente con el RUT: " + dto.getRut());
        }
        Model_pacientes paciente = mapper.toEntity(dto);
        Model_pacientes guardado = repository.save(paciente);
        return mapper.toResponse(guardado);
    }

    /* Buscar por RUT */
    public PacienteResponseDTO obtenerPorRut(String rut) {
        Model_pacientes paciente = repository.findByRut(rut)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Paciente no encontrado con RUT " + rut));
        return mapper.toResponse(paciente);
    }

    /* Buscar por previsión */
    public List<PacienteResponseDTO> obtenerPorPrevision(String prevision) {
        List<Model_pacientes> pacientes = repository.findByPrevision(prevision);
        if (pacientes.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron pacientes con la previsión: " + prevision);
        }
        return pacientes.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /* Actualizar */
    public PacienteResponseDTO actualizar(Long id, PacienteRequestDTO dto) {
        Model_pacientes paciente = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Paciente no encontrado con ID " + id));

        Optional<Model_pacientes> existente = repository.findByRut(dto.getRut());
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new DuplicadoException("El RUT ya está en uso por otro paciente");
        }

        mapper.updateEntity(paciente, dto);
        Model_pacientes actualizado = repository.save(paciente);
        return mapper.toResponse(actualizado);
    }

    /* Eliminar */
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar, ID no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
