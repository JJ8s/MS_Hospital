package com.hospital.ms_medicos.controller;

import com.hospital.ms_medicos.dto.request.MedicoRequestDTO;
import com.hospital.ms_medicos.dto.response.MedicoResponseDTO;
import com.hospital.ms_medicos.service.Service_medicos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

@WebMvcTest(Controller_medicos.class)
@AutoConfigureMockMvc(addFilters = false)
class Controller_medicosTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Service_medicos serviceMedicos;

    @Test
    void listarMedicos_retornaOkConLista() throws Exception {
        when(serviceMedicos.obtenerTodos()).thenReturn(List.of(medicoResponse()));

        mockMvc.perform(get("/api/medicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rut").value("11111111-1"));

        verify(serviceMedicos).obtenerTodos();
    }

    @Test
    void crearMedico_datosValidos_retornaCreated() throws Exception {
        when(serviceMedicos.guardar(any(MedicoRequestDTO.class))).thenReturn(medicoResponse());

        mockMvc.perform(post("/api/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "11111111-1",
                                  "nombre": "Andrea",
                                  "apellido": "Soto",
                                  "especialidad": "Cardiologia",
                                  "sector": "Urgencias",
                                  "email": "andrea.soto@hospital.com",
                                  "telefono": "+56912345678",
                                  "sueldo": 1500000,
                                  "fecha_contratacion": "2022-03-01",
                                  "annios_edad": 35,
                                  "annios_experiencia": 8
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Andrea"))
                .andExpect(jsonPath("$.especialidad").value("Cardiologia"));

        verify(serviceMedicos).guardar(any(MedicoRequestDTO.class));
    }

    @Test
    void crearMedico_datosInvalidos_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "",
                                  "nombre": "",
                                  "apellido": "Soto",
                                  "especialidad": "Cardiologia",
                                  "sector": "Urgencias",
                                  "email": "andrea.soto@hospital.com",
                                  "telefono": "+56912345678",
                                  "sueldo": 1500000,
                                  "fecha_contratacion": "2022-03-01",
                                  "annios_edad": 35,
                                  "annios_experiencia": 8
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(serviceMedicos, never()).guardar(any(MedicoRequestDTO.class));
    }

    @Test
    void buscarPorId_retornaOk() throws Exception {
        when(serviceMedicos.obtenerPorId(1L)).thenReturn(medicoResponse());

        mockMvc.perform(get("/api/medicos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(serviceMedicos).obtenerPorId(1L);
    }

    @Test
    void buscarPorRut_retornaOk() throws Exception {
        when(serviceMedicos.obtenerPorRut("11111111-1")).thenReturn(medicoResponse());

        mockMvc.perform(get("/api/medicos/rut/{rut}", "11111111-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("11111111-1"));

        verify(serviceMedicos).obtenerPorRut("11111111-1");
    }

    @Test
    void buscarPorEspecialidad_retornaOk() throws Exception {
        when(serviceMedicos.obtenerPorEspecialidad("Cardiologia")).thenReturn(List.of(medicoResponse()));

        mockMvc.perform(get("/api/medicos/especialidad/{especialidad}", "Cardiologia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].especialidad").value("Cardiologia"));

        verify(serviceMedicos).obtenerPorEspecialidad("Cardiologia");
    }

    @Test
    void actualizar_retornaOk() throws Exception {
        when(serviceMedicos.actualizar(any(Long.class), any(MedicoRequestDTO.class))).thenReturn(medicoResponse());

        mockMvc.perform(put("/api/medicos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "11111111-1",
                                  "nombre": "Andrea",
                                  "apellido": "Soto",
                                  "especialidad": "Cardiologia",
                                  "sector": "Urgencias",
                                  "email": "andrea.soto@hospital.com",
                                  "telefono": "+56912345678",
                                  "sueldo": 1500000,
                                  "fecha_contratacion": "2022-03-01",
                                  "annios_edad": 35,
                                  "annios_experiencia": 8
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(serviceMedicos).actualizar(any(Long.class), any(MedicoRequestDTO.class));
    }

    @Test
    void eliminar_retornaNoContent() throws Exception {
        doNothing().when(serviceMedicos).eliminar(1L);

        mockMvc.perform(delete("/api/medicos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(serviceMedicos).eliminar(1L);
    }

    private MedicoResponseDTO medicoResponse() {
        MedicoResponseDTO response = new MedicoResponseDTO();
        response.setId(1L);
        response.setRut("11111111-1");
        response.setNombre("Andrea");
        response.setApellido("Soto");
        response.setEspecialidad("Cardiologia");
        response.setSector("Urgencias");
        response.setEmail("andrea.soto@hospital.com");
        response.setTelefono("+56912345678");
        response.setSueldo(1500000.0);
        response.setFecha_contratacion(LocalDate.of(2022, 3, 1));
        response.setAnnios_edad(35);
        response.setAnnios_experiencia(8);
        return response;
    }
}
