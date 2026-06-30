package com.hospital.ms_facturacion.service;

import com.hospital.ms_facturacion.client.InventarioClient;
import com.hospital.ms_facturacion.client.RecetaClient;
import com.hospital.ms_facturacion.dto.ProductoDTO;
import com.hospital.ms_facturacion.dto.RecetaDTO;
import com.hospital.ms_facturacion.dto.request.FacturaRequestDTO;
import com.hospital.ms_facturacion.dto.request.FacturaUpdateDTO;
import com.hospital.ms_facturacion.dto.response.FacturaResponseDTO;
import com.hospital.ms_facturacion.exception.EstadoFacturaException;
import com.hospital.ms_facturacion.exception.FacturaDuplicadaException;
import com.hospital.ms_facturacion.exception.ResourceNotFoundException;
import com.hospital.ms_facturacion.mapper.FacturaMapper;
import com.hospital.ms_facturacion.model.Factura;
import com.hospital.ms_facturacion.repository.FacturaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private RecetaClient recetaClient;

    @Mock
    private InventarioClient inventarioClient;

    @Spy
    private FacturaMapper facturaMapper;

    @InjectMocks
    private FacturaService facturaService;

    @Test
    void listarTodas_retornaFacturas() {
        when(facturaRepository.findAll()).thenReturn(List.of(factura()));

        List<FacturaResponseDTO> resultado = facturaService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals(2000.0, resultado.get(0).getMontoTotal());
        verify(facturaRepository, times(1)).findAll();
    }

    @Test
    void crearFactura_calculaMontoCorrectamente() {
        FacturaRequestDTO request = request();

        when(facturaRepository.findByRecetaId(10L)).thenReturn(Optional.empty());
        when(recetaClient.obtenerPorId(10L)).thenReturn(ResponseEntity.ok(receta()));
        when(inventarioClient.obtenerPorId(5L)).thenReturn(ResponseEntity.ok(producto()));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> {
            Factura factura = invocation.getArgument(0);
            factura.setId(1L);
            return factura;
        });

        FacturaResponseDTO resultado = facturaService.crearFactura(request);

        assertEquals(2000.0, resultado.getMontoTotal());
        assertEquals(100L, resultado.getPacienteId());
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(recetaClient, times(1)).obtenerPorId(10L);
        verify(inventarioClient, times(1)).obtenerPorId(5L);
        verify(facturaRepository, times(1)).save(any(Factura.class));
    }

    @Test
    void crearFactura_siYaExisteParaReceta_lanzaExcepcion() {
        when(facturaRepository.findByRecetaId(10L)).thenReturn(Optional.of(factura()));

        assertThrows(FacturaDuplicadaException.class, () -> facturaService.crearFactura(request()));
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(facturaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> facturaService.obtenerPorId(99L));
    }

    @Test
    void pagarFactura_cambiaEstadoAPagada() {
        Factura factura = factura();
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FacturaResponseDTO resultado = facturaService.pagarFactura(1L);

        assertEquals("PAGADA", resultado.getEstado());
        verify(facturaRepository, times(1)).save(factura);
    }

    @Test
    void pagarFactura_pagada_lanzaExcepcion() {
        Factura factura = factura();
        factura.setEstado("PAGADA");
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        assertThrows(EstadoFacturaException.class, () -> facturaService.pagarFactura(1L));
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    void actualizarFactura_recalculaMonto() {
        FacturaUpdateDTO update = new FacturaUpdateDTO();
        update.setCostoServicio(750.0);

        Factura factura = factura();
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FacturaResponseDTO resultado = facturaService.actualizarFactura(1L, update);

        assertEquals(2250.0, resultado.getMontoTotal());
        assertEquals(750.0, resultado.getCostoServicio());
    }

    @Test
    void eliminarFactura_pagada_lanzaExcepcion() {
        Factura factura = factura();
        factura.setEstado("PAGADA");
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

        assertThrows(EstadoFacturaException.class, () -> facturaService.eliminarFactura(1L));
        verify(facturaRepository, never()).delete(any(Factura.class));
    }

    @Test
    void eliminarFactura_pendiente_elimina() {
        Factura factura = factura();
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
        doNothing().when(facturaRepository).delete(factura);

        assertDoesNotThrow(() -> facturaService.eliminarFactura(1L));
        verify(facturaRepository, times(1)).delete(factura);
    }

    private FacturaRequestDTO request() {
        FacturaRequestDTO request = new FacturaRequestDTO();
        request.setRecetaId(10L);
        request.setCostoServicio(500.0);
        return request;
    }

    private RecetaDTO receta() {
        RecetaDTO receta = new RecetaDTO();
        receta.setId(10L);
        receta.setPacienteId(100L);
        receta.setProductoId(5L);
        return receta;
    }

    private ProductoDTO producto() {
        ProductoDTO producto = new ProductoDTO();
        producto.setId(5L);
        producto.setPrecio(1500.0);
        return producto;
    }

    private Factura factura() {
        Factura factura = new Factura();
        factura.setId(1L);
        factura.setRecetaId(10L);
        factura.setPacienteId(100L);
        factura.setCostoServicio(500.0);
        factura.setMontoTotal(2000.0);
        factura.setEstado("PENDIENTE");
        factura.setFechaEmision(LocalDateTime.now());
        return factura;
    }
}
