package com.hospital.ms_notificaciones.controller;

import com.hospital.ms_notificaciones.dto.request.NotificacionRequestDTO;
import com.hospital.ms_notificaciones.dto.response.NotificacionResponseDTO;
import com.hospital.ms_notificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacionService notificacionService;

    @Test
    void listar_retornaOkConLista() throws Exception {
        when(notificacionService.listarTodas()).thenReturn(List.of(notificacionResponse("PENDIENTE")));

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(notificacionService).listarTodas();
    }

    @Test
    void crear_datosValidos_retornaCreated() throws Exception {
        when(notificacionService.crear(any(NotificacionRequestDTO.class))).thenReturn(notificacionResponse("PENDIENTE"));

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destinatarioId": 1,
                                  "tipoDestinatario": "PACIENTE",
                                  "canal": "APP",
                                  "asunto": "Recordatorio de cita",
                                  "mensaje": "Tiene una cita medica agendada para manana."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destinatarioId").value(1L))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(notificacionService).crear(any(NotificacionRequestDTO.class));
    }

    @Test
    void crear_datosInvalidos_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destinatarioId": 1,
                                  "tipoDestinatario": "PACIENTE",
                                  "canal": "WHATSAPP",
                                  "asunto": "Ok",
                                  "mensaje": "Hola"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(notificacionService, never()).crear(any(NotificacionRequestDTO.class));
    }

    @Test
    void obtenerPorId_retornaOk() throws Exception {
        when(notificacionService.obtenerPorId(1L)).thenReturn(notificacionResponse("PENDIENTE"));

        mockMvc.perform(get("/api/notificaciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(notificacionService).obtenerPorId(1L);
    }

    @Test
    void listarPorDestinatario_retornaOk() throws Exception {
        when(notificacionService.listarPorDestinatario(1L)).thenReturn(List.of(notificacionResponse("PENDIENTE")));

        mockMvc.perform(get("/api/notificaciones/destinatario/{destinatarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].destinatarioId").value(1L));

        verify(notificacionService).listarPorDestinatario(1L);
    }

    @Test
    void listarPorEstado_retornaOk() throws Exception {
        when(notificacionService.listarPorEstado("PENDIENTE")).thenReturn(List.of(notificacionResponse("PENDIENTE")));

        mockMvc.perform(get("/api/notificaciones/estado/{estado}", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(notificacionService).listarPorEstado("PENDIENTE");
    }

    @Test
    void marcarComoEnviada_retornaOk() throws Exception {
        when(notificacionService.marcarComoEnviada(1L)).thenReturn(notificacionResponse("ENVIADA"));

        mockMvc.perform(post("/api/notificaciones/{id}/enviar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));

        verify(notificacionService).marcarComoEnviada(1L);
    }

    @Test
    void marcarComoLeida_retornaOk() throws Exception {
        when(notificacionService.marcarComoLeida(1L)).thenReturn(notificacionResponse("LEIDA"));

        mockMvc.perform(post("/api/notificaciones/{id}/leer", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LEIDA"));

        verify(notificacionService).marcarComoLeida(1L);
    }

    @Test
    void eliminar_retornaNoContent() throws Exception {
        doNothing().when(notificacionService).eliminar(1L);

        mockMvc.perform(delete("/api/notificaciones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(notificacionService).eliminar(1L);
    }

    private NotificacionResponseDTO notificacionResponse(String estado) {
        return new NotificacionResponseDTO(
                1L,
                1L,
                "PACIENTE",
                "APP",
                "Recordatorio de cita",
                "Tiene una cita medica agendada para manana.",
                estado,
                LocalDateTime.of(2099, 7, 1, 10, 30),
                null
        );
    }
}
