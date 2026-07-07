package com.hospital.ms_urgencia.service;

import com.hospital.ms_urgencia.client.PacienteCliente;
import com.hospital.ms_urgencia.dto.request.UrgenciaRequestDTO;
import com.hospital.ms_urgencia.dto.response.UrgenciaResponseDTO;
import com.hospital.ms_urgencia.exception.NivelTriageInvalidoException;
import com.hospital.ms_urgencia.exception.ResourceNotFoundException;
import com.hospital.ms_urgencia.mapper.UrgenciaMapper;
import com.hospital.ms_urgencia.model.Model_urgencias;
import com.hospital.ms_urgencia.model.NivelTriage;
import com.hospital.ms_urgencia.repository.Repository_urgencias;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public UrgenciaResponseDTO obtenerPorId(Long id) {
        Model_urgencias urgencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de urgencia no encontrado con ID " + id));

        return mapper.toResponse(urgencia);
    }

    public List<UrgenciaResponseDTO> obtenerPorPaciente(Long pacienteId) {
        validarPacienteExiste(pacienteId);

        return repository.findByPacienteId(pacienteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public UrgenciaResponseDTO guardar(UrgenciaRequestDTO dto) {
        validarPacienteExiste(dto.getPacienteId());

        Model_urgencias urgencia = mapper.toEntity(dto);
        Model_urgencias guardada = repository.save(urgencia);

        return mapper.toResponse(guardada);
    }

    public UrgenciaResponseDTO actualizarTriage(Long id, String nuevoNivel) {
        Model_urgencias urgencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de urgencia no encontrado con ID " + id));

        NivelTriage nivel = convertirNivelTriage(nuevoNivel);
        urgencia.setNivelTriage(nivel);

        return mapper.toResponse(repository.save(urgencia));
    }

    public UrgenciaResponseDTO cerrarUrgencia(Long id) {
        Model_urgencias urgencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de urgencia no encontrado con ID " + id));

        urgencia.setEstadoActual("ATENDIDA");

        return mapper.toResponse(repository.save(urgencia));
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: el registro de urgencia no existe con ID " + id);
        }

        repository.deleteById(id);
    }

    private void validarPacienteExiste(Long pacienteId) {
        try {
            pacienteCliente.obtenerPacientePorId(pacienteId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Paciente con ID " + pacienteId + " no existe");
        }
    }

    private NivelTriage convertirNivelTriage(String nuevoNivel) {
        if (nuevoNivel == null || nuevoNivel.isBlank()) {
            throw new NivelTriageInvalidoException("El nivel de triage es obligatorio");
        }

        try {
            return NivelTriage.valueOf(nuevoNivel.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new NivelTriageInvalidoException(
                    "Nivel de triage no válido: " + nuevoNivel + ". Valores permitidos: ROJO, NARANJA, AMARILLO, VERDE, AZUL"
            );
        }
    }
}
