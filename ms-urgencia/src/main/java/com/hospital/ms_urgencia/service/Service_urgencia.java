package com.hospital.ms_urgencia.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.ms_urgencia.client.PacienteCliente;
import com.hospital.ms_urgencia.dto.request.UrgenciaRequestDTO;
import com.hospital.ms_urgencia.dto.response.UrgenciaResponseDTO;
import com.hospital.ms_urgencia.exception.NivelTriageInvalidoException;
import com.hospital.ms_urgencia.exception.ResourceNotFoundExceptione;
import com.hospital.ms_urgencia.mapper.UrgenciaMapper;
import com.hospital.ms_urgencia.model.Model_urgencias;
import com.hospital.ms_urgencia.model.NivelTriage;
import com.hospital.ms_urgencia.repository.Repository_urgencias;

import feign.FeignException;

@Service
public class Service_urgencia {

    private final Repository_urgencias repository;
    private final UrgenciaMapper mapper;
    private final PacienteCliente pacienteCliente;

    public Service_urgencia(Repository_urgencias repository, UrgenciaMapper mapper, PacienteCliente pacienteCliente) {
        this.repository = repository;
        this.mapper = mapper;
        this.pacienteCliente = pacienteCliente;
    }

    public List<UrgenciaResponseDTO> obtenerTodasLasUrgencias() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public UrgenciaResponseDTO obtenerPorId(Long id) {
        Model_urgencias urgencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptione("Registro de urgencia no encontrado con ID " + id));
        return mapper.toResponse(urgencia);
    }

    public UrgenciaResponseDTO guardar(UrgenciaRequestDTO dto) {
        try {
            pacienteCliente.obtenerPacientePorId(dto.getPacienteId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundExceptione("Paciente con id " + dto.getPacienteId() + " no existe");
        }
        Model_urgencias urgencia = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(urgencia));
    }

    public UrgenciaResponseDTO actualizarTriage(Long id, String nuevoNivel) {
        Model_urgencias urgencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptione("Registro de urgencias no encontrado " + id));
        try {
            NivelTriage nivel = NivelTriage.valueOf(nuevoNivel.toUpperCase());
            urgencia.setNivelTriage(nivel);
        } catch (IllegalArgumentException e) {
            throw new NivelTriageInvalidoException("Nivel de triage no valido: " + nuevoNivel + ". Valores permitidos: ROJO, NARANJA, AMARILLO, VERDE, AZUL");
        }
        return mapper.toResponse(repository.save(urgencia));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundExceptione("No se puede eliminar: Id no existe " + id);
        }
        repository.deleteById(id);
    }
}