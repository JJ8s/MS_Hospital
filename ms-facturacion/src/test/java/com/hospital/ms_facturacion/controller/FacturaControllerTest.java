package com.hospital.ms_facturacion.controller;

import com.hospital.ms_facturacion.dto.request.FacturaRequestDTO;
import com.hospital.ms_facturacion.dto.request.FacturaUpdateDTO;
import com.hospital.ms_facturacion.dto.response.FacturaResponseDTO;
import com.hospital.ms_facturacion.service.FacturaService;
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

@WebMvcTest(FacturaController.class)
@AutoConfigureMockMvc(addFilters = false)
class FacturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaService facturaService;

    @Test
    void cuandoListarFacturas_debeRetornarOk() throws Exception {
        when(facturaService.listarTodas()).thenReturn(List.of(facturaResponse("PENDIENTE")));

        mockMvc.perform(get("/api/facturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(facturaService).listarTodas();
    }

    @Test
    void cuandoCrearFacturaConDatosValidos_debeRetornarCreated() throws Exception {
        when(facturaService.crearFactura(any(FacturaRequestDTO.class))).thenReturn(facturaResponse("PENDIENTE"));

        mockMvc.perform(post("/api/facturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recetaId": 1,
                                  "costoServicio": 500
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recetaId").value(1L))
                .andExpect(jsonPath("$.montoTotal").value(2000.0));

        verify(facturaService).crearFactura(any(FacturaRequestDTO.class));
    }

    @Test
    void cuandoPagarFactura_debeRetornarOk() throws Exception {
        when(facturaService.pagarFactura(1L)).thenReturn(facturaResponse("PAGADA"));

        mockMvc.perform(post("/api/facturas/{id}/pagar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADA"));

        verify(facturaService).pagarFactura(1L);
    }

    @Test
    void cuandoBuscarFacturaPorId_debeRetornarOk() throws Exception {
        when(facturaService.obtenerPorId(1L)).thenReturn(facturaResponse("PENDIENTE"));

        mockMvc.perform(get("/api/facturas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pacienteId").value(1L));

        verify(facturaService).obtenerPorId(1L);
    }

    @Test
    void cuandoActualizarFactura_debeRetornarOk() throws Exception {
        FacturaResponseDTO response = facturaResponse("PENDIENTE");
        response.setCostoServicio(750.0);
        response.setMontoTotal(2250.0);
        when(facturaService.actualizarFactura(any(Long.class), any(FacturaUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/facturas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"costoServicio\":750}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoServicio").value(750.0))
                .andExpect(jsonPath("$.montoTotal").value(2250.0));

        verify(facturaService).actualizarFactura(any(Long.class), any(FacturaUpdateDTO.class));
    }

    @Test
    void cuandoEliminarFactura_debeRetornarNoContent() throws Exception {
        doNothing().when(facturaService).eliminarFactura(1L);

        mockMvc.perform(delete("/api/facturas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(facturaService).eliminarFactura(1L);
    }

    private FacturaResponseDTO facturaResponse(String estado) {
        return new FacturaResponseDTO(
                1L,
                1L,
                1L,
                500.0,
                2000.0,
                estado,
                LocalDateTime.of(2026, 6, 29, 22, 30)
        );
    }
}
