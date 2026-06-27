package hospital.ms_facturacion.service;

import hospital.ms_facturacion.client.InventarioClient;
import hospital.ms_facturacion.client.RecetaClient;
import hospital.ms_facturacion.model.Factura;
import hospital.ms_facturacion.repository.FacturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
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
        // GIVEN: Preparamos los mocks
        when(facturaRepository.findByRecetaId(10L)).thenReturn(Optional.empty());
        
        
        Map<String, Object> mockReceta = new HashMap<>();
        mockReceta.put("productoId", 5L);
        mockReceta.put("pacienteId", 100L);
        
        when(recetaClient.obtenerPorId(10L)).thenReturn(ResponseEntity.ok(mockReceta));

        Map<String, Object> mockProducto = new HashMap<>();
        mockProducto.put("precio", 1500.0);
        when(inventarioClient.obtenerPorId(5L)).thenReturn(ResponseEntity.ok(mockProducto));

        when(facturaRepository.save(any(Factura.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN: Ejecutamos la lógica de creación
        Factura resultado = facturaService.crearFactura(facturaMock);

        // THEN: Verificamos cálculos
        assertNotNull(resultado);
        assertEquals(2000.0, resultado.getMontoTotal());
        assertEquals("PENDIENTE", resultado.getEstado());
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