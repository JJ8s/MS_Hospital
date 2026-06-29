package com.hospital.ms_medicos.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.ms_medicos.dto.request.MedicoRequestDTO;
import com.hospital.ms_medicos.dto.response.MedicoResponseDTO;
import com.hospital.ms_medicos.exception.DuplicadoException;
import com.hospital.ms_medicos.exception.ResourceNotFoundException;
import com.hospital.ms_medicos.mapper.MedicoMapper;
import com.hospital.ms_medicos.model.Model_medicos;
import com.hospital.ms_medicos.repository.Repository_medicos;

@Service
public class Service_medicos {

    private final Repository_medicos repository_medicos;
    private final MedicoMapper mapper;

    public Service_medicos(Repository_medicos repository_medicos,
                        MedicoMapper mapper) {
        this.repository_medicos = repository_medicos;
        this.mapper = mapper;
    }

    /*Obtener todos*/
    public List<MedicoResponseDTO> obtenerTodos() {
        return repository_medicos.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /*Obtener por ID*/
    public MedicoResponseDTO obtenerPorId(Long id) {

        Model_medicos medico = repository_medicos.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medico no encontrado con ID " + id));

        return mapper.toResponse(medico);
    }

    /*Guardar*/
    public MedicoResponseDTO guardar(MedicoRequestDTO dto) {

        if (repository_medicos.findByRut(dto.getRut()).isPresent()) {
            throw new DuplicadoException("Ya existe un medico con este RUT " + dto.getRut());
        }

        Model_medicos medico = mapper.toEntity(dto);

        Model_medicos guardado = repository_medicos.save(medico);

        return mapper.toResponse(guardado);
    }

    /*Buscar por RUT*/
    public MedicoResponseDTO obtenerPorRut(String rut) {

        Model_medicos medico = repository_medicos.findByRut(rut)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medico no encontrado con Rut " + rut));

        return mapper.toResponse(medico);
    }

    /*Buscar por especialidad*/
    public List<MedicoResponseDTO> obtenerPorEspecialidad(String especialidad) {

        List<Model_medicos> medicos = repository_medicos.findByEspecialidad(especialidad);

        if (medicos.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron medicos con la especialidad " + especialidad);
        }

        return medicos.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /*Actualizar*/
    public MedicoResponseDTO actualizar(Long id, MedicoRequestDTO dto) {

        Model_medicos medico = repository_medicos.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medico no encontrado con ID " + id));

        Optional<Model_medicos> existente = repository_medicos.findByRut(dto.getRut());

        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new DuplicadoException("El RUT ya está en uso por otro medico");
        }

        mapper.updateEntity(medico, dto);

        Model_medicos actualizado = repository_medicos.save(medico);

        return mapper.toResponse(actualizado);
    }

    /* Eliminar */
    public void eliminar(Long id) {

        if (!repository_medicos.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No se puede eliminar, ID no encontrado: " + id);
        }

        repository_medicos.deleteById(id);
    }
}