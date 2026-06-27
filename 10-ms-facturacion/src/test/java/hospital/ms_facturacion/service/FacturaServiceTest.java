package hospital.ms_facturacion.service;

import hospital.ms_facturacion.client.InventarioClient;
import hospital.ms_facturacion.client.RecetaClient;
import hospital.ms_facturacion.dto.ProductoDTO;
import hospital.ms_facturacion.dto.RecetaDTO;
import hospital.ms_facturacion.model.Factura;
import hospital.ms_facturacion.repository.FacturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private RecetaClient recetaClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private FacturaService facturaService;

    private Factura facturaMock;

    @BeforeEach
    void setUp() {
        facturaMock = new Factura();
        facturaMock.setId(1L);
        facturaMock.setRecetaId(10L);
        facturaMock.setCostoServicio(500.0);
        facturaMock.setEstado("PENDIENTE");
    }

    @Test
    void cuandoCrearFactura_entoncesCalculaMontoCorrectamente() {
        
        when(facturaRepository.findByRecetaId(10L)).thenReturn(Optional.empty());
        
        
        RecetaDTO mockReceta = new RecetaDTO();
        mockReceta.setId(10L);
        mockReceta.setProductoId(5L);
        mockReceta.setPacienteId(100L);
        
        
        when(recetaClient.obtenerPorId(10L)).thenReturn(ResponseEntity.ok(mockReceta));

        
        ProductoDTO mockProducto = new ProductoDTO();
        mockProducto.setId(5L);
        mockProducto.setPrecio(1500.0);
        
        
        when(inventarioClient.obtenerPorId(5L)).thenReturn(ResponseEntity.ok(mockProducto));

        
        when(facturaRepository.save(any(Factura.class))).thenAnswer(i -> i.getArgument(0));

        
        Factura resultado = facturaService.crearFactura(facturaMock);

        
        assertNotNull(resultado);
        
        assertEquals(2000.0, resultado.getMontoTotal(), "El monto total calculado es incorrecto");
        assertEquals(100L, resultado.getPacienteId(), "El ID del paciente no se reconcilió correctamente");
        assertEquals("PENDIENTE", resultado.getEstado());
        
        
        verify(recetaClient, times(1)).obtenerPorId(10L);
        verify(inventarioClient, times(1)).obtenerPorId(5L);
        verify(facturaRepository, times(1)).save(any(Factura.class));
    }

    @Test
    void cuandoFacturaYaExisteParaReceta_entoncesLanzaExcepcion() {
        // GIVEN
        when(facturaRepository.findByRecetaId(10L)).thenReturn(Optional.of(new Factura()));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            facturaService.crearFactura(facturaMock);
        });

        assertTrue(ex.getMessage().contains("Ya existe una factura emitida"));
        verify(facturaRepository, never()).save(any());
    }

    @Test
    void cuandoPagarFactura_entoncesCambiaEstadoAPagada() {
        // GIVEN
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaMock));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        Factura resultado = facturaService.pagarFactura(1L);

        // THEN
        assertEquals("PAGADA", resultado.getEstado());
        verify(facturaRepository, times(1)).save(any());
    }

    @Test
    void cuandoEliminarFacturaPagada_entoncesLanzaExcepcionPorSeguridad() {
        // GIVEN: Una factura que ya está pagada no puede ser eliminada
        facturaMock.setEstado("PAGADA");
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaMock));

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            facturaService.eliminarFactura(1L);
        });

        assertTrue(ex.getMessage().contains("prohibido eliminar facturas pagadas"));
        verify(facturaRepository, never()).delete(any());
    }

    @Test
    void cuandoEliminarFacturaPendiente_entoncesExito() {
        // GIVEN
        when(facturaRepository.findById(1L)).thenReturn(Optional.of(facturaMock));
        doNothing().when(facturaRepository).delete(any());

        // WHEN & THEN
        assertDoesNotThrow(() -> facturaService.eliminarFactura(1L));
        verify(facturaRepository, times(1)).delete(facturaMock);
    }
}