package com.hospital.ms_citas.controller;

import com.hospital.ms_citas.dto.request.CitaRequestDTO;
import com.hospital.ms_citas.dto.response.CitaResponseDTO;
import com.hospital.ms_citas.model.EstadoCita;
import com.hospital.ms_citas.service.Service_citas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller_citas.class)
@AutoConfigureMockMvc(addFilters = false)
class Controller_citasTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Service_citas serviceCitas;

    @Test
    void listarCitas_retornaOkConLista() throws Exception {
        when(serviceCitas.listarTodas()).thenReturn(List.of(citaResponse(EstadoCita.PENDIENTE)));

        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(serviceCitas).listarTodas();
    }

    @Test
    void buscarPorId_retornaOk() throws Exception {
        when(serviceCitas.obtenerPorId(1L)).thenReturn(citaResponse(EstadoCita.PENDIENTE));

        mockMvc.perform(get("/api/citas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(serviceCitas).obtenerPorId(1L);
    }

    @Test
    void buscarPorPaciente_retornaOk() throws Exception {
        when(serviceCitas.obtenerPorPaciente(10L)).thenReturn(List.of(citaResponse(EstadoCita.PENDIENTE)));

        mockMvc.perform(get("/api/citas/paciente/{pacienteId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pacienteId").value(10L));

        verify(serviceCitas).obtenerPorPaciente(10L);
    }

    @Test
    void buscarPorMedico_retornaOk() throws Exception {
        when(serviceCitas.obtenerPorMedico(7L)).thenReturn(List.of(citaResponse(EstadoCita.PENDIENTE)));

        mockMvc.perform(get("/api/citas/medico/{medicoId}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicoId").value(7L));

        verify(serviceCitas).obtenerPorMedico(7L);
    }

    @Test
    void buscarPorEstado_retornaOk() throws Exception {
        when(serviceCitas.obtenerPorEstado(EstadoCita.PENDIENTE)).thenReturn(List.of(citaResponse(EstadoCita.PENDIENTE)));

        mockMvc.perform(get("/api/citas/estado/{estado}", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(serviceCitas).obtenerPorEstado(EstadoCita.PENDIENTE);
    }

    @Test
    void agendarCita_datosValidos_retornaCreated() throws Exception {
        when(serviceCitas.agendarCita(any(CitaRequestDTO.class))).thenReturn(citaResponse(EstadoCita.PENDIENTE));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 10,
                                  "medicoId": 7,
                                  "fecha": "2099-07-01",
                                  "hora": "10:30:00",
                                  "estado": "PENDIENTE",
                                  "observaciones": "Control general"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacienteId").value(10L))
                .andExpect(jsonPath("$.medicoId").value(7L));

        verify(serviceCitas).agendarCita(any(CitaRequestDTO.class));
    }

    @Test
    void agendarCita_datosInvalidos_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 10,
                                  "medicoId": 7,
                                  "hora": "10:30:00",
                                  "estado": "PENDIENTE",
                                  "observaciones": "Control general"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(serviceCitas, never()).agendarCita(any(CitaRequestDTO.class));
    }

    @Test
    void actualizar_retornaOk() throws Exception {
        when(serviceCitas.actualizar(any(Long.class), any(CitaRequestDTO.class))).thenReturn(citaResponse(EstadoCita.PENDIENTE));

        mockMvc.perform(put("/api/citas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 10,
                                  "medicoId": 7,
                                  "fecha": "2099-07-01",
                                  "hora": "10:30:00",
                                  "estado": "PENDIENTE",
                                  "observaciones": "Control general"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(serviceCitas).actualizar(any(Long.class), any(CitaRequestDTO.class));
    }

    @Test
    void cancelarCita_retornaOk() throws Exception {
        when(serviceCitas.cancelarCita(1L)).thenReturn(citaResponse(EstadoCita.CANCELADA));

        mockMvc.perform(patch("/api/citas/{id}/cancelar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        verify(serviceCitas).cancelarCita(1L);
    }

    @Test
    void eliminarCita_retornaNoContent() throws Exception {
        doNothing().when(serviceCitas).eliminarFisicamente(1L);

        mockMvc.perform(delete("/api/citas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(serviceCitas).eliminarFisicamente(1L);
    }

    private CitaResponseDTO citaResponse(EstadoCita estado) {
        return new CitaResponseDTO(
                1L,
                10L,
                7L,
                LocalDate.of(2099, 7, 1),
                LocalTime.of(10, 30),
                estado,
                "Control general"
        );
    }
}
