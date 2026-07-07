package com.hospital.ms_recetas.controller;

import com.hospital.ms_recetas.dto.request.RecetaRequestDTO;
import com.hospital.ms_recetas.dto.request.RecetaUpdateDTO;
import com.hospital.ms_recetas.dto.response.RecetaResponseDTO;
import com.hospital.ms_recetas.service.RecetaService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecetaController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecetaService recetaService;

    @Test
    void cuandoListarRecetas_debeRetornarOk() throws Exception {
        when(recetaService.listarTodas()).thenReturn(List.of(recetaResponse()));

        mockMvc.perform(get("/api/recetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctorResponsable").value("Dr. Matias Rodriguez"));

        verify(recetaService).listarTodas();
    }

    @Test
    void cuandoCrearRecetaConDatosValidos_debeRetornarCreated() throws Exception {
        when(recetaService.guardar(any(RecetaRequestDTO.class))).thenReturn(recetaResponse());

        mockMvc.perform(post("/api/recetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pacienteId": 1,
                                  "productoId": 1,
                                  "cantidad": 2,
                                  "indicaciones": "Tomar 1 comprimido cada 8 horas por 5 dias.",
                                  "doctorResponsable": "Dr. Matias Rodriguez"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pacienteId").value(1L))
                .andExpect(jsonPath("$.cantidad").value(2));

        verify(recetaService).guardar(any(RecetaRequestDTO.class));
    }

    @Test
    void cuandoObtenerRecetaPorId_debeRetornarOk() throws Exception {
        when(recetaService.obtenerPorId(1L)).thenReturn(recetaResponse());

        mockMvc.perform(get("/api/recetas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productoId").value(1L));

        verify(recetaService).obtenerPorId(1L);
    }

    @Test
    void cuandoActualizarReceta_debeRetornarOk() throws Exception {
        RecetaResponseDTO response = recetaResponse();
        response.setIndicaciones("Tomar con abundante agua despues de cada comida.");
        response.setDoctorResponsable("Dra. Ana Lopez");
        when(recetaService.actualizarIndicaciones(any(Long.class), any(RecetaUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/recetas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "indicaciones": "Tomar con abundante agua despues de cada comida.",
                                  "doctorResponsable": "Dra. Ana Lopez"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorResponsable").value("Dra. Ana Lopez"));

        verify(recetaService).actualizarIndicaciones(any(Long.class), any(RecetaUpdateDTO.class));
    }

    @Test
    void cuandoEliminarReceta_debeRetornarNoContent() throws Exception {
        doNothing().when(recetaService).eliminar(1L);

        mockMvc.perform(delete("/api/recetas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(recetaService).eliminar(1L);
    }

    private RecetaResponseDTO recetaResponse() {
        return new RecetaResponseDTO(
                1L,
                1L,
                1L,
                2,
                "Tomar 1 comprimido cada 8 horas por 5 dias.",
                "Dr. Matias Rodriguez",
                LocalDateTime.of(2026, 6, 29, 22, 30)
        );
    }
}
