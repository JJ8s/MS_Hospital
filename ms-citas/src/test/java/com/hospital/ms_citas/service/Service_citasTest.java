package com.hospital.ms_citas.service;

import com.hospital.ms_citas.client.MedicoCliente;
import com.hospital.ms_citas.client.PacienteCliente;
import com.hospital.ms_citas.dto.external.MedicoResponseDTO;
import com.hospital.ms_citas.dto.external.PacienteResponseDTO;
import com.hospital.ms_citas.dto.request.CitaRequestDTO;
import com.hospital.ms_citas.dto.response.CitaResponseDTO;
import com.hospital.ms_citas.exception.HorarioNoDisponibleException;
import com.hospital.ms_citas.exception.ResourceNotFoundException;
import com.hospital.ms_citas.mapper.CitaMapper;
import com.hospital.ms_citas.model.EstadoCita;
import com.hospital.ms_citas.model.Model_citas;
import com.hospital.ms_citas.repository.Repository_citas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Service_citasTest {

    @Mock
    private Repository_citas repository;

    @Mock
    private MedicoCliente medicoCliente;

    @Mock
    private PacienteCliente pacienteCliente;

    private CitaMapper mapper;
    private Service_citas service;

    @BeforeEach
    void setUp() {
        mapper = new CitaMapper();
        service = new Service_citas(repository, mapper, medicoCliente, pacienteCliente);
    }

    @Test
    @DisplayName("listarTodas debe retornar citas mapeadas a DTO")
    void listarTodas_conCitasExistentes_retornaListaMapeada() {
        // Given
        Model_citas cita = crearCita(1L, 10L, 7L, EstadoCita.PENDIENTE);
        when(repository.findAll()).thenReturn(List.of(cita));

        // When
        List<CitaResponseDTO> resultado = service.listarTodas();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(10L);
        assertThat(resultado.get(0).getMedicoId()).isEqualTo(7L);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("obtenerPorId debe retornar una cita cuando existe")
    void obtenerPorId_idExistente_retornaCita() {
        // Given
        Model_citas cita = crearCita(1L, 10L, 7L, EstadoCita.PENDIENTE);
        when(repository.findById(1L)).thenReturn(Optional.of(cita));

        // When
        CitaResponseDTO resultado = service.obtenerPorId(1L);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.PENDIENTE);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId debe lanzar excepción cuando no existe")
    void obtenerPorId_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cita no encontrada");
    }

    @Test
    @DisplayName("obtenerPorPaciente debe retornar citas asociadas al paciente")
    void obtenerPorPaciente_conCitas_retornaLista() {
        // Given
        when(repository.findByPacienteId(10L))
                .thenReturn(List.of(crearCita(1L, 10L, 7L, EstadoCita.PENDIENTE)));

        // When
        List<CitaResponseDTO> resultado = service.obtenerPorPaciente(10L);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("obtenerPorPaciente debe lanzar excepción cuando no existen citas")
    void obtenerPorPaciente_sinCitas_lanzaExcepcion() {
        // Given
        when(repository.findByPacienteId(10L)).thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorPaciente(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontraron citas");
    }

    @Test
    @DisplayName("obtenerPorMedico debe retornar citas asociadas al médico")
    void obtenerPorMedico_conCitas_retornaLista() {
        // Given
        when(repository.findByMedicoId(7L))
                .thenReturn(List.of(crearCita(1L, 10L, 7L, EstadoCita.PENDIENTE)));

        // When
        List<CitaResponseDTO> resultado = service.obtenerPorMedico(7L);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMedicoId()).isEqualTo(7L);
    }

    @Test
    @DisplayName("obtenerPorMedico debe lanzar excepción cuando no existen citas")
    void obtenerPorMedico_sinCitas_lanzaExcepcion() {
        // Given
        when(repository.findByMedicoId(7L)).thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorMedico(7L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontraron citas");
    }

    @Test
    @DisplayName("obtenerPorEstado debe retornar citas filtradas por estado")
    void obtenerPorEstado_conCitas_retornaLista() {
        // Given
        when(repository.findByEstado(EstadoCita.PENDIENTE))
                .thenReturn(List.of(crearCita(1L, 10L, 7L, EstadoCita.PENDIENTE)));

        // When
        List<CitaResponseDTO> resultado = service.obtenerPorEstado(EstadoCita.PENDIENTE);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoCita.PENDIENTE);
    }

    @Test
    @DisplayName("obtenerPorEstado debe lanzar excepción cuando no hay citas")
    void obtenerPorEstado_sinCitas_lanzaExcepcion() {
        // Given
        when(repository.findByEstado(EstadoCita.REALIZADA)).thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorEstado(EstadoCita.REALIZADA))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontraron citas");
    }

    @Test
    @DisplayName("agendarCita debe validar médico, paciente, horario y guardar")
    void agendarCita_datosValidos_guardaCorrectamente() {
        // Given
        CitaRequestDTO request = crearRequest(10L, 7L, EstadoCita.PENDIENTE);
        when(medicoCliente.obtenerMedicoPorId(7L)).thenReturn(new MedicoResponseDTO());
        when(pacienteCliente.obtenerPacientePorId(10L)).thenReturn(new PacienteResponseDTO());
        when(repository.findByMedicoIdAndFechaAndHoraAndEstadoNot(
                request.getMedicoId(), request.getFecha(), request.getHora(), EstadoCita.CANCELADA))
                .thenReturn(List.of());
        when(repository.save(any(Model_citas.class))).thenAnswer(invocation -> {
            Model_citas cita = invocation.getArgument(0);
            cita.setId(1L);
            return cita;
        });

        // When
        CitaResponseDTO resultado = service.agendarCita(request);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPacienteId()).isEqualTo(10L);
        assertThat(resultado.getMedicoId()).isEqualTo(7L);
        verify(medicoCliente).obtenerMedicoPorId(7L);
        verify(pacienteCliente).obtenerPacientePorId(10L);
        verify(repository).save(any(Model_citas.class));
    }

    @Test
    @DisplayName("agendarCita debe lanzar excepción si el médico tiene conflicto horario")
    void agendarCita_horarioOcupado_lanzaExcepcion() {
        // Given
        CitaRequestDTO request = crearRequest(10L, 7L, EstadoCita.PENDIENTE);
        when(medicoCliente.obtenerMedicoPorId(7L)).thenReturn(new MedicoResponseDTO());
        when(pacienteCliente.obtenerPacientePorId(10L)).thenReturn(new PacienteResponseDTO());
        when(repository.findByMedicoIdAndFechaAndHoraAndEstadoNot(
                request.getMedicoId(), request.getFecha(), request.getHora(), EstadoCita.CANCELADA))
                .thenReturn(List.of(crearCita(2L, 20L, 7L, EstadoCita.PENDIENTE)));

        // When / Then
        assertThatThrownBy(() -> service.agendarCita(request))
                .isInstanceOf(HorarioNoDisponibleException.class)
                .hasMessageContaining("ya tiene una cita");
        verify(repository, never()).save(any(Model_citas.class));
    }

    @Test
    @DisplayName("actualizar debe modificar una cita existente")
    void actualizar_datosValidos_actualizaCorrectamente() {
        // Given
        CitaRequestDTO request = crearRequest(10L, 7L, EstadoCita.PENDIENTE);
        Model_citas existente = crearCita(1L, 11L, 8L, EstadoCita.PENDIENTE);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(medicoCliente.obtenerMedicoPorId(7L)).thenReturn(new MedicoResponseDTO());
        when(pacienteCliente.obtenerPacientePorId(10L)).thenReturn(new PacienteResponseDTO());
        when(repository.findByMedicoIdAndFechaAndHoraAndEstadoNotAndIdNot(
                request.getMedicoId(), request.getFecha(), request.getHora(), EstadoCita.CANCELADA, 1L))
                .thenReturn(List.of());
        when(repository.save(any(Model_citas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CitaResponseDTO resultado = service.actualizar(1L, request);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPacienteId()).isEqualTo(10L);
        assertThat(resultado.getMedicoId()).isEqualTo(7L);
        verify(repository).save(existente);
    }

    @Test
    @DisplayName("actualizar debe lanzar excepción si la cita no existe")
    void actualizar_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.actualizar(99L, crearRequest(10L, 7L, EstadoCita.PENDIENTE)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cita no encontrada");
        verify(repository, never()).save(any(Model_citas.class));
    }

    @Test
    @DisplayName("cancelarCita debe cambiar el estado a CANCELADA")
    void cancelarCita_idExistente_cancelaCorrectamente() {
        // Given
        Model_citas cita = crearCita(1L, 10L, 7L, EstadoCita.PENDIENTE);
        when(repository.findById(1L)).thenReturn(Optional.of(cita));
        when(repository.save(any(Model_citas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CitaResponseDTO resultado = service.cancelarCita(1L);

        // Then
        assertThat(resultado.getEstado()).isEqualTo(EstadoCita.CANCELADA);
        verify(repository).save(cita);
    }

    @Test
    @DisplayName("cancelarCita debe lanzar excepción si la cita no existe")
    void cancelarCita_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.cancelarCita(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    @DisplayName("eliminarFisicamente debe eliminar cita existente")
    void eliminarFisicamente_idExistente_eliminaCorrectamente() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.eliminarFisicamente(1L);

        // Then
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarFisicamente debe lanzar excepción si no existe")
    void eliminarFisicamente_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> service.eliminarFisicamente(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ID no encontrado");
        verify(repository, never()).deleteById(anyLong());
    }

    private Model_citas crearCita(Long id, Long pacienteId, Long medicoId, EstadoCita estado) {
        Model_citas cita = new Model_citas();
        cita.setId(id);
        cita.setPacienteId(pacienteId);
        cita.setMedicoId(medicoId);
        cita.setFecha(LocalDate.now().plusDays(5));
        cita.setHora(LocalTime.of(10, 30));
        cita.setEstado(estado);
        cita.setObservaciones("Control general");
        return cita;
    }

    private CitaRequestDTO crearRequest(Long pacienteId, Long medicoId, EstadoCita estado) {
        return new CitaRequestDTO(
                pacienteId,
                medicoId,
                LocalDate.now().plusDays(5),
                LocalTime.of(10, 30),
                estado,
                "Control general"
        );
    }
}
