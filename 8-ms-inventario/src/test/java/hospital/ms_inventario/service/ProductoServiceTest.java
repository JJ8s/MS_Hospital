package hospital.ms_inventario.service;

import hospital.ms_inventario.model.Producto;
import hospital.ms_inventario.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void cuandoBuscarPorId_entoncesRetornaProducto() {
        // GIVEN
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Paracetamol 500mg");
        producto.setLote("LT-2026-A1");
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // WHEN
        Producto resultado = productoService.buscarPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals("Paracetamol 500mg", resultado.getNombre());
        assertEquals("LT-2026-A1", resultado.getLote());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void cuandoBuscarIdInexistente_entoncesLanzaExcepcion() {
        // GIVEN
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            productoService.buscarPorId(99L);
        });
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void cuandoListarTodos_entoncesRetornaListaDeProductos() {
        // GIVEN
        Producto p1 = new Producto();
        p1.setNombre("Producto A");
        Producto p2 = new Producto();
        p2.setNombre("Producto B");
        
        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // WHEN
        List<Producto> lista = productoService.listarTodos();

        // THEN
        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void cuandoGuardarProducto_entoncesRetornaProductoGuardado() {
        // GIVEN
        Producto producto = new Producto();
        producto.setNombre("Ibuprofeno");
        producto.setStock(50);
        producto.setPrecio(2000.0);
        producto.setFechaVencimiento(LocalDate.of(2027, 12, 31));

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // WHEN
        Producto guardado = productoService.guardar(producto);

        // THEN
        assertNotNull(guardado);
        assertEquals("Ibuprofeno", guardado.getNombre());
        assertEquals(50, guardado.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void cuandoEliminarProducto_entoncesLlamaAlRepositorio() {
        // GIVEN (Configuración de datos y comportamiento de Mocks)
        Long idEliminar = 1L;
        
        
        when(productoRepository.existsById(idEliminar)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(idEliminar);

        // WHEN (Ejecución de la acción real en el servicio)
        productoService.eliminar(idEliminar);

        // THEN (Verificación de resultados e interacciones)
        verify(productoRepository, times(1)).existsById(idEliminar);
        verify(productoRepository, times(1)).deleteById(idEliminar);
    }

    @Test
    void cuandoGuardarProductoConStockNegativo_entoncesLanzaExcepcion() {
        // GIVEN
        Producto productoInvalido = new Producto();
        productoInvalido.setNombre("Jarabe");
        productoInvalido.setStock(-5); // Stock inválido

        // WHEN & THEN
        // Verificamos que se lance la excepción IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.guardar(productoInvalido);
        });

        assertEquals("El stock no puede ser negativo", exception.getMessage());
        // Verificamos que el repositorio NUNCA se llamó, ya que la regla de negocio lo detuvo
        verify(productoRepository, never()).save(any(Producto.class));
    }
}
