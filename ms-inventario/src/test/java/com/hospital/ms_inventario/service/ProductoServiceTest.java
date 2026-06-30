package com.hospital.ms_inventario.service;

import com.hospital.ms_inventario.dto.request.ProductoRequestDTO;
import com.hospital.ms_inventario.dto.response.ProductoResponseDTO;
import com.hospital.ms_inventario.exception.ProductoDuplicadoException;
import com.hospital.ms_inventario.exception.ResourceNotFoundException;
import com.hospital.ms_inventario.exception.StockInsuficienteException;
import com.hospital.ms_inventario.mapper.ProductoMapper;
import com.hospital.ms_inventario.model.Producto;
import com.hospital.ms_inventario.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Spy
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarTodos_retornaProductos() {
        Producto producto = producto(1L, "Paracetamol 500mg", "LT-2026-A1", 99);
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponseDTO> resultado = productoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Paracetamol 500mg", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_existente_retornaProducto() {
        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto(1L, "Ibuprofeno", "LT-2026-B1", 40)));

        ProductoResponseDTO resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Ibuprofeno", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_inexistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.buscarPorId(99L));
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void guardar_valido_retornaProductoCreado() {
        ProductoRequestDTO request = request("Suero fisiologico", "LT-2027-A1", 80);
        Producto guardado = producto(1L, "Suero fisiologico", "LT-2027-A1", 80);

        when(productoRepository.findByNombreIgnoreCase("Suero fisiologico")).thenReturn(Optional.empty());
        when(productoRepository.findByLote("LT-2027-A1")).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponseDTO resultado = productoService.guardar(request);

        assertEquals(1L, resultado.getId());
        assertEquals("Suero fisiologico", resultado.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void guardar_nombreDuplicado_lanzaExcepcion() {
        ProductoRequestDTO request = request("Paracetamol 500mg", "LT-2027-Z1", 15);
        when(productoRepository.findByNombreIgnoreCase("Paracetamol 500mg"))
                .thenReturn(Optional.of(producto(1L, "Paracetamol 500mg", "LT-2026-A1", 99)));

        assertThrows(ProductoDuplicadoException.class, () -> productoService.guardar(request));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void reducirStock_conStockSuficiente_actualizaStock() {
        Producto producto = producto(1L, "Paracetamol 500mg", "LT-2026-A1", 20);
        Producto actualizado = producto(1L, "Paracetamol 500mg", "LT-2026-A1", 15);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        ProductoResponseDTO resultado = productoService.reducirStock(1L, 5);

        assertEquals(15, resultado.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void reducirStock_sinStockSuficiente_lanzaExcepcion() {
        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto(1L, "Paracetamol 500mg", "LT-2026-A1", 2)));

        assertThrows(StockInsuficienteException.class, () -> productoService.reducirStock(1L, 5));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminar_existente_eliminaProducto() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminar(1L);

        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    private Producto producto(Long id, String nombre, String lote, Integer stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion("Descripcion de prueba");
        producto.setPrecio(1500.0);
        producto.setStock(stock);
        producto.setLote(lote);
        producto.setFechaVencimiento(LocalDate.now().plusYears(1));
        return producto;
    }

    private ProductoRequestDTO request(String nombre, String lote, Integer stock) {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre(nombre);
        request.setDescripcion("Descripcion de prueba");
        request.setPrecio(1500.0);
        request.setStock(stock);
        request.setLote(lote);
        request.setFechaVencimiento(LocalDate.now().plusYears(1));
        return request;
    }
}
