package com.hospital.ms_citas.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.hospital.ms_citas.client.MedicoCliente;
import com.hospital.ms_citas.client.PacienteCliente;
import com.hospital.ms_citas.dto.request.CitaRequestDTO;
import com.hospital.ms_citas.dto.response.CitaResponseDTO;
import com.hospital.ms_citas.exception.HorarioNoDisponibleException;
import com.hospital.ms_citas.exception.ResourceNotFoundException;
import com.hospital.ms_citas.mapper.CitaMapper;
import com.hospital.ms_citas.model.EstadoCita;
import com.hospital.ms_citas.model.Model_citas;
import com.hospital.ms_citas.repository.Repository_citas;

@Service
public class Service_citas {

    private final Repository_citas repository;
    private final CitaMapper mapper;
    private final MedicoCliente medicoCliente;
    private final PacienteCliente pacienteCliente;

    public Service_citas(Repository_citas repository, CitaMapper mapper,
            MedicoCliente medicoCliente, PacienteCliente pacienteCliente) {
        this.repository = repository;
        this.mapper = mapper;
        this.medicoCliente = medicoCliente;
        this.pacienteCliente = pacienteCliente;
    }

    public List<CitaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public CitaResponseDTO obtenerPorId(Long id) {
        Model_citas cita = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID " + id));
        return mapper.toResponse(cita);
    }

    public List<CitaResponseDTO> obtenerPorPaciente(Long pacienteId) {
        List<Model_citas> citas = repository.findByPacienteId(pacienteId);
        if (citas.isEmpty()) throw new ResourceNotFoundException("No se encontraron citas para el paciente con ID " + pacienteId);
        return citas.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public List<CitaResponseDTO> obtenerPorMedico(Long medicoId) {
        List<Model_citas> citas = repository.findByMedicoId(medicoId);
        if (citas.isEmpty()) throw new ResourceNotFoundException("No se encontraron citas para el médico con ID " + medicoId);
        return citas.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public List<CitaResponseDTO> obtenerPorEstado(EstadoCita estado) {
        List<Model_citas> citas = repository.findByEstado(estado);
        if (citas.isEmpty()) throw new ResourceNotFoundException("No se encontraron citas con estado " + estado);
        return citas.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public CitaResponseDTO agendarCita(CitaRequestDTO dto) {
        medicoCliente.obtenerMedicoPorId(dto.getMedicoId());
        pacienteCliente.obtenerPacientePorId(dto.getPacienteId());

        List<Model_citas> conflictos = repository.findByMedicoIdAndFechaAndHoraAndEstadoNot(
                dto.getMedicoId(), dto.getFecha(), dto.getHora(), EstadoCita.CANCELADA);
        if (!conflictos.isEmpty()) {
            throw new HorarioNoDisponibleException(
                    "El médico ya tiene una cita agendada el " + dto.getFecha() + " a las " + dto.getHora());
        }

        Model_citas cita = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(cita));
    }

    public CitaResponseDTO cancelarCita(Long id) {
        Model_citas cita = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede cancelar: cita con ID " + id + " no encontrada"));
        cita.setEstado(EstadoCita.CANCELADA);
        return mapper.toResponse(repository.save(cita));
    }

    public CitaResponseDTO actualizar(Long id, CitaRequestDTO dto) {
        Model_citas cita = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID " + id));
        mapper.updateEntity(cita, dto);
        return mapper.toResponse(repository.save(cita));
    }

    public void eliminarFisicamente(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("No se puede eliminar, ID no encontrado: " + id);
        repository.deleteById(id);
    }
}