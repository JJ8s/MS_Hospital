package com.hospital.ms_urgencia.controller;

import com.hospital.ms_urgencia.dto.request.UrgenciaRequestDTO;
import com.hospital.ms_urgencia.dto.response.UrgenciaResponseDTO;
import com.hospital.ms_urgencia.model.NivelTriage;
import com.hospital.ms_urgencia.service.Service_urgencia;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller_urgencias.class)
@AutoConfigureMockMvc(addFilters = false)
class Controller_urgenciasTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Service_urgencia serviceUrgencia;

    @Test
    void listar_retornaOkConLista() throws Exception {
        when(serviceUrgencia.obtenerTodasLasUrgencias()).thenReturn(List.of(urgenciaResponse(NivelTriage.AMARILLO, "EN_ESPERA")));

        mockMvc.perform(get("/api/urgencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nivelTriage").value("AMARILLO"));

        verify(serviceUrgencia).obtenerTodasLasUrgencias();
    }

    @Test
    void buscarPorId_retornaOk() throws Exception {
        when(serviceUrgencia.obtenerPorId(1L)).thenReturn(urgenciaResponse(NivelTriage.VERDE, "EN_ESPERA"));

        mockMvc.perform(get("/api/urgencias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(serviceUrgencia).obtenerPorId(1L);
    }

    @Test
    void buscarPorPaciente_retornaOk() throws Exception {
        when(serviceUrgencia.obtenerPorPaciente(10L)).thenReturn(List.of(urgenciaResponse(NivelTriage.NARANJA, "EN_ESPERA")));

        mockMvc.perform(get("/api/urgencias/paciente/{pacienteId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(10L));

        verify(serviceUrgencia).obtenerPorPaciente(10L);
    }

    @Test
    void crear_datosValidos_retornaCreated() throws Exception {
        when(serviceUrgencia.guardar(any(UrgenciaRequestDTO.class))).thenReturn(urgenciaResponse(NivelTriage.ROJO, "EN_ESPERA"));

        mockMvc.perform(post("/api/urgencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 10,
                                  "nivelTriage": "ROJO",
                                  "motivoIngreso": "Dolor toracico"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacienteId").value(10L))
                .andExpect(jsonPath("$.nivelTriage").value("ROJO"));

        verify(serviceUrgencia).guardar(any(UrgenciaRequestDTO.class));
    }

    @Test
    void crear_datosInvalidos_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/urgencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 10,
                                  "nivelTriage": "ROJO",
                                  "motivoIngreso": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(serviceUrgencia, never()).guardar(any(UrgenciaRequestDTO.class));
    }

    @Test
    void actualizarTriage_retornaOk() throws Exception {
        when(serviceUrgencia.actualizarTriage(1L, "ROJO")).thenReturn(urgenciaResponse(NivelTriage.ROJO, "EN_ESPERA"));

        mockMvc.perform(patch("/api/urgencias/{id}/triage", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivel\":\"ROJO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelTriage").value("ROJO"));

        verify(serviceUrgencia).actualizarTriage(1L, "ROJO");
    }

    @Test
    void cerrarUrgencia_retornaOk() throws Exception {
        when(serviceUrgencia.cerrarUrgencia(1L)).thenReturn(urgenciaResponse(NivelTriage.AMARILLO, "ATENDIDA"));

        mockMvc.perform(patch("/api/urgencias/{id}/cerrar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActual").value("ATENDIDA"));

        verify(serviceUrgencia).cerrarUrgencia(1L);
    }

    @Test
    void eliminar_retornaNoContent() throws Exception {
        doNothing().when(serviceUrgencia).eliminar(1L);

        mockMvc.perform(delete("/api/urgencias/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(serviceUrgencia).eliminar(1L);
    }

    private UrgenciaResponseDTO urgenciaResponse(NivelTriage nivel, String estado) {
        return new UrgenciaResponseDTO(
                1L,
                10L,
                nivel,
                "Motivo de prueba",
                LocalDateTime.of(2099, 7, 1, 10, 30),
                estado
        );
    }
}
