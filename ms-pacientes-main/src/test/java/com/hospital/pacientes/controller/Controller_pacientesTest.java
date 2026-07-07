package com.hospital.pacientes.controller;

import com.hospital.pacientes.dto.request.PacienteRequestDTO;
import com.hospital.pacientes.dto.response.PacienteResponseDTO;
import com.hospital.pacientes.service.Service_pacientes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller_pacientes.class)
@AutoConfigureMockMvc(addFilters = false)
class Controller_pacientesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Service_pacientes servicePacientes;

    @Test
    void listarPacientes_retornaOkConLista() throws Exception {
        when(servicePacientes.obtenerTodos()).thenReturn(List.of(pacienteResponse()));

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rut").value("12345678-9"));

        verify(servicePacientes).obtenerTodos();
    }

    @Test
    void crearPaciente_datosValidos_retornaCreated() throws Exception {
        when(servicePacientes.guardar(any(PacienteRequestDTO.class))).thenReturn(pacienteResponse());

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "12345678-9",
                                  "nombre": "Carlos",
                                  "apellido": "Rojas",
                                  "edad": 30,
                                  "telefono": "+56911112222",
                                  "prevision": "FONASA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.prevision").value("FONASA"));

        verify(servicePacientes).guardar(any(PacienteRequestDTO.class));
    }

    @Test
    void crearPaciente_datosInvalidos_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "",
                                  "nombre": "Carlos",
                                  "apellido": "Rojas",
                                  "edad": 151,
                                  "telefono": "+56911112222",
                                  "prevision": "FONASA"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(servicePacientes, never()).guardar(any(PacienteRequestDTO.class));
    }

    @Test
    void buscarPorId_retornaOk() throws Exception {
        when(servicePacientes.obtenerPorId(1L)).thenReturn(pacienteResponse());

        mockMvc.perform(get("/api/pacientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(servicePacientes).obtenerPorId(1L);
    }

    @Test
    void buscarPorRut_retornaOk() throws Exception {
        when(servicePacientes.obtenerPorRut("12345678-9")).thenReturn(pacienteResponse());

        mockMvc.perform(get("/api/pacientes/rut/{rut}", "12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"));

        verify(servicePacientes).obtenerPorRut("12345678-9");
    }

    @Test
    void buscarPorPrevision_retornaOk() throws Exception {
        when(servicePacientes.obtenerPorPrevision("FONASA")).thenReturn(List.of(pacienteResponse()));

        mockMvc.perform(get("/api/pacientes/prevision/{prevision}", "FONASA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prevision").value("FONASA"));

        verify(servicePacientes).obtenerPorPrevision("FONASA");
    }

    @Test
    void actualizar_retornaOk() throws Exception {
        when(servicePacientes.actualizar(any(Long.class), any(PacienteRequestDTO.class))).thenReturn(pacienteResponse());

        mockMvc.perform(put("/api/pacientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "12345678-9",
                                  "nombre": "Carlos",
                                  "apellido": "Rojas",
                                  "edad": 30,
                                  "telefono": "+56911112222",
                                  "prevision": "FONASA"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(servicePacientes).actualizar(any(Long.class), any(PacienteRequestDTO.class));
    }

    @Test
    void eliminar_retornaNoContent() throws Exception {
        doNothing().when(servicePacientes).eliminar(1L);

        mockMvc.perform(delete("/api/pacientes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(servicePacientes).eliminar(1L);
    }

    private PacienteResponseDTO pacienteResponse() {
        PacienteResponseDTO response = new PacienteResponseDTO();
        response.setId(1L);
        response.setRut("12345678-9");
        response.setNombre("Carlos");
        response.setApellido("Rojas");
        response.setEdad(30);
        response.setTelefono("+56911112222");
        response.setPrevision("FONASA");
        return response;
    }
}
