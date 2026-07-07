package com.hospital.ms_notificaciones.service;

import com.hospital.ms_notificaciones.dto.request.NotificacionRequestDTO;
import com.hospital.ms_notificaciones.dto.response.NotificacionResponseDTO;
import com.hospital.ms_notificaciones.exception.EstadoNotificacionException;
import com.hospital.ms_notificaciones.exception.ResourceNotFoundException;
import com.hospital.ms_notificaciones.mapper.NotificacionMapper;
import com.hospital.ms_notificaciones.model.Notificacion;
import com.hospital.ms_notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Spy
    private NotificacionMapper notificacionMapper;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    void listarTodas_retornaNotificaciones() {
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion("PENDIENTE")));

        List<NotificacionResponseDTO> resultado = notificacionService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Recordatorio de cita", resultado.get(0).getAsunto());
        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    void crear_guardaNotificacionPendiente() {
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> {
            Notificacion notificacion = invocation.getArgument(0);
            notificacion.setId(1L);
            notificacion.setFechaCreacion(LocalDateTime.now());
            return notificacion;
        });

        NotificacionResponseDTO resultado = notificacionService.crear(request());

        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificacionService.obtenerPorId(99L));
    }

    @Test
    void marcarComoEnviada_actualizaEstadoYFecha() {
        Notificacion notificacion = notificacion("PENDIENTE");
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificacionResponseDTO resultado = notificacionService.marcarComoEnviada(1L);

        assertEquals("ENVIADA", resultado.getEstado());
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    @Test
    void marcarComoLeida_siEstaPendiente_lanzaExcepcion() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion("PENDIENTE")));

        assertThrows(EstadoNotificacionException.class, () -> notificacionService.marcarComoLeida(1L));
    }

    @Test
    void eliminar_existente_eliminaNotificacion() {
        Notificacion notificacion = notificacion("ENVIADA");
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        doNothing().when(notificacionRepository).delete(notificacion);

        notificacionService.eliminar(1L);

        verify(notificacionRepository, times(1)).delete(notificacion);
    }

    private NotificacionRequestDTO request() {
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setDestinatarioId(1L);
        request.setTipoDestinatario("PACIENTE");
        request.setCanal("APP");
        request.setAsunto("Recordatorio de cita");
        request.setMensaje("Tiene una cita medica agendada para manana a las 10:30.");
        return request;
    }

    private Notificacion notificacion(String estado) {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setDestinatarioId(1L);
        notificacion.setTipoDestinatario("PACIENTE");
        notificacion.setCanal("APP");
        notificacion.setAsunto("Recordatorio de cita");
        notificacion.setMensaje("Tiene una cita medica agendada para manana a las 10:30.");
        notificacion.setEstado(estado);
        notificacion.setFechaCreacion(LocalDateTime.now());
        return notificacion;
    }
}
