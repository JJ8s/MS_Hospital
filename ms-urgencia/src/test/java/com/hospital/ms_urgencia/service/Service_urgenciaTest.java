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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Service_urgenciaTest {

    @Mock
    private Repository_urgencias repository;

    @Mock
    private PacienteCliente pacienteCliente;

    private Service_urgencia service;

    @BeforeEach
    void setUp() {
        service = new Service_urgencia(repository, new UrgenciaMapper(), pacienteCliente);
    }

    @Test
    void obtenerTodasLasUrgencias_conDatos_retornaListaMapeada() {
        // Given
        Model_urgencias urgencia = crearUrgencia(1L, 10L, NivelTriage.AMARILLO);
        when(repository.findAll()).thenReturn(List.of(urgencia));

        // When
        List<UrgenciaResponseDTO> resultado = service.obtenerTodasLasUrgencias();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(10L);
        assertThat(resultado.get(0).getNivelTriage()).isEqualTo(NivelTriage.AMARILLO);
    }

    @Test
    void obtenerPorId_idExistente_retornaUrgencia() {
        // Given
        Model_urgencias urgencia = crearUrgencia(1L, 10L, NivelTriage.VERDE);
        when(repository.findById(1L)).thenReturn(Optional.of(urgencia));

        // When
        UrgenciaResponseDTO resultado = service.obtenerPorId(1L);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNivelTriage()).isEqualTo(NivelTriage.VERDE);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void obtenerPorPaciente_pacienteExistente_retornaLista() {
        // Given
        Model_urgencias urgencia = crearUrgencia(1L, 10L, NivelTriage.NARANJA);
        when(repository.findByPacienteId(10L)).thenReturn(List.of(urgencia));

        // When
        List<UrgenciaResponseDTO> resultado = service.obtenerPorPaciente(10L);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(10L);
        verify(pacienteCliente).obtenerPacientePorId(10L);
    }

    @Test
    void guardar_pacienteExistente_guardaUrgencia() {
        // Given
        UrgenciaRequestDTO dto = new UrgenciaRequestDTO(10L, NivelTriage.ROJO, "Dolor torácico");
        Model_urgencias guardada = crearUrgencia(1L, 10L, NivelTriage.ROJO);
        guardada.setMotivoIngreso("Dolor torácico");
        when(repository.save(any(Model_urgencias.class))).thenReturn(guardada);

        // When
        UrgenciaResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPacienteId()).isEqualTo(10L);
        assertThat(resultado.getNivelTriage()).isEqualTo(NivelTriage.ROJO);
        verify(pacienteCliente).obtenerPacientePorId(10L);
        verify(repository).save(any(Model_urgencias.class));
    }

    @Test
    void guardar_pacienteInexistente_lanzaExcepcionYNoGuarda() {
        // Given
        UrgenciaRequestDTO dto = new UrgenciaRequestDTO(99L, NivelTriage.AMARILLO, "Dolor abdominal");
        doThrow(org.mockito.Mockito.mock(FeignException.NotFound.class))
                .when(pacienteCliente).obtenerPacientePorId(99L);

        // When / Then
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any(Model_urgencias.class));
    }

    @Test
    void actualizarTriage_nivelValido_actualizaCorrectamente() {
        // Given
        Model_urgencias urgencia = crearUrgencia(1L, 10L, NivelTriage.VERDE);
        Model_urgencias actualizada = crearUrgencia(1L, 10L, NivelTriage.ROJO);
        when(repository.findById(1L)).thenReturn(Optional.of(urgencia));
        when(repository.save(urgencia)).thenReturn(actualizada);

        // When
        UrgenciaResponseDTO resultado = service.actualizarTriage(1L, "ROJO");

        // Then
        assertThat(resultado.getNivelTriage()).isEqualTo(NivelTriage.ROJO);
        verify(repository).save(urgencia);
    }

    @Test
    void actualizarTriage_nivelInvalido_lanzaExcepcion() {
        // Given
        Model_urgencias urgencia = crearUrgencia(1L, 10L, NivelTriage.VERDE);
        when(repository.findById(1L)).thenReturn(Optional.of(urgencia));

        // When / Then
        assertThatThrownBy(() -> service.actualizarTriage(1L, "MORADO"))
                .isInstanceOf(NivelTriageInvalidoException.class)
                .hasMessageContaining("MORADO");

        verify(repository, never()).save(any(Model_urgencias.class));
    }

    @Test
    void actualizarTriage_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(88L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.actualizarTriage(88L, "ROJO"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("88");
    }

    @Test
    void cerrarUrgencia_idExistente_cambiaEstadoAAtendida() {
        // Given
        Model_urgencias urgencia = crearUrgencia(1L, 10L, NivelTriage.AMARILLO);
        Model_urgencias cerrada = crearUrgencia(1L, 10L, NivelTriage.AMARILLO);
        cerrada.setEstadoActual("ATENDIDA");
        when(repository.findById(1L)).thenReturn(Optional.of(urgencia));
        when(repository.save(urgencia)).thenReturn(cerrada);

        // When
        UrgenciaResponseDTO resultado = service.cerrarUrgencia(1L);

        // Then
        assertThat(resultado.getEstadoActual()).isEqualTo("ATENDIDA");
        verify(repository).save(urgencia);
    }

    @Test
    void eliminar_idExistente_eliminaCorrectamente() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.eliminar(1L);

        // Then
        verify(repository).deleteById(1L);
    }

    @Test
    void eliminar_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.existsById(77L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> service.eliminar(77L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("77");

        verify(repository, never()).deleteById(77L);
    }

    private Model_urgencias crearUrgencia(Long id, Long pacienteId, NivelTriage nivelTriage) {
        Model_urgencias urgencia = new Model_urgencias();
        urgencia.setId(id);
        urgencia.setPacienteId(pacienteId);
        urgencia.setNivelTriage(nivelTriage);
        urgencia.setMotivoIngreso("Motivo de prueba");
        urgencia.setFechaIngreso(LocalDateTime.of(2026, 7, 1, 10, 30));
        urgencia.setEstadoActual("EN_ESPERA");
        return urgencia;
    }
}
