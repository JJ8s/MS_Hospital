package com.hospital.ms_inventario.controller;

import com.hospital.ms_inventario.dto.request.ProductoRequestDTO;
import com.hospital.ms_inventario.dto.response.ProductoResponseDTO;
import com.hospital.ms_inventario.service.ProductoService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void cuandoListarProductos_debeRetornarOk() throws Exception {
        ProductoResponseDTO producto = productoResponse();
        when(productoService.listarTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Paracetamol 500mg"));

        verify(productoService).listarTodos();
    }

    @Test
    void cuandoObtenerProductoPorId_debeRetornarOk() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(productoResponse());

        mockMvc.perform(get("/api/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.stock").value(99));

        verify(productoService).buscarPorId(1L);
    }

    @Test
    void cuandoCrearProductoConDatosValidos_debeRetornarCreated() throws Exception {
        when(productoService.guardar(any(ProductoRequestDTO.class))).thenReturn(productoResponse());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Paracetamol 500mg",
                                  "descripcion": "Analgesico y antipiretico basico",
                                  "precio": 1500,
                                  "stock": 99,
                                  "lote": "LT-2026-A1",
                                  "fechaVencimiento": "2027-12-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Paracetamol 500mg"))
                .andExpect(jsonPath("$.lote").value("LT-2026-A1"));

        verify(productoService).guardar(any(ProductoRequestDTO.class));
    }

    @Test
    void cuandoReducirStock_debeRetornarOk() throws Exception {
        ProductoResponseDTO producto = productoResponse();
        producto.setStock(94);
        when(productoService.reducirStock(1L, 5)).thenReturn(producto);

        mockMvc.perform(put("/api/productos/{id}/reducir-stock", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(94));

        verify(productoService).reducirStock(1L, 5);
    }

    @Test
    void cuandoEliminarProducto_debeRetornarNoContent() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/productos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }

    private ProductoResponseDTO productoResponse() {
        return new ProductoResponseDTO(
                1L,
                "Paracetamol 500mg",
                "Analgesico y antipiretico basico",
                1500.0,
                99,
                "LT-2026-A1",
                LocalDate.of(2027, 12, 31)
        );
    }
}
