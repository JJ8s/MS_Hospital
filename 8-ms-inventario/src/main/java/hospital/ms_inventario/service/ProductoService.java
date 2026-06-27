package hospital.ms_inventario.service;

import hospital.ms_inventario.exception.LoteDuplicadoException;
import hospital.ms_inventario.exception.ProductoDuplicadoException;
import hospital.ms_inventario.exception.StockInsuficienteException;
import hospital.ms_inventario.model.Producto;
import hospital.ms_inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional
    public Producto guardar(Producto producto) {
        // 1. Regla: Evitar duplicados por nombre
        if (productoRepository.existsByNombre(producto.getNombre())) {
            throw new ProductoDuplicadoException("Error de Negocio: El producto '" + producto.getNombre() + "' ya está registrado.");
        }

        // 2. Regla de Trazabilidad: Lote único
        if (productoRepository.existsByLote(producto.getLote())) {
            throw new LoteDuplicadoException("Error de Trazabilidad: El lote '" + producto.getLote() + "' ya existe en el sistema.");
        }

        // 3. Regla de Integridad: Stock inicial no negativo
        if (producto.getStock() < 0) {
            throw new StockInsuficienteException("Error de Integridad: El stock inicial no puede ser negativo (" + producto.getStock() + ").");
        }

        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Error: No se encontró el producto con ID " + id));
        
        // Verifica si el producto está próximo a vencer (menos de 30 días)
        alertarSiVencePronto(producto);
        
        return producto;
    }

    @Transactional
    public Producto actualizar(Long id, Producto detallesActualizados) {
        Producto productoExistente = buscarPorId(id);

        // Validar que el nuevo stock no sea negativo antes de actualizar [2]
        validarStockNoNegativo(detallesActualizados.getStock());

        //Si el lote cambia, verificar que el nuevo lote no esté ocupado
        if (!productoExistente.getLote().equals(detallesActualizados.getLote())) {
            validarLoteUnico(detallesActualizados.getLote());
        }

        productoExistente.setNombre(detallesActualizados.getNombre());
        productoExistente.setLote(detallesActualizados.getLote());
        productoExistente.setFechaVencimiento(detallesActualizados.getFechaVencimiento());
        productoExistente.setPrecio(detallesActualizados.getPrecio());
        productoExistente.setStock(detallesActualizados.getStock());
        
        return productoRepository.save(productoExistente);
    }

    // --- MÉTODOS DE APOYO PARA LÓGICA DE NEGOCIO ---

    private void validarStockNoNegativo(Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Integridad de Datos: El stock no puede ser negativo (" + stock + ").");
        }
    }

    private void validarLoteUnico(String lote) {
        if (productoRepository.findByLote(lote).isPresent()) {
            throw new RuntimeException("Error de Trazabilidad: El lote '" + lote + "' ya existe en el sistema.");
        }
    }

    private void alertarSiVencePronto(Producto producto) {
        if (producto.getFechaVencimiento() != null) {
            long diasParaVencer = ChronoUnit.DAYS.between(LocalDate.now(), producto.getFechaVencimiento());
            if (diasParaVencer <= 30 && diasParaVencer >= 0) {
                
                System.out.println("ALERTA SANITARIA: El producto " + producto.getNombre() + " vence en " + diasParaVencer + " días.");
            }
        }
    }

    // Ajustar Stock 
    @Transactional
    public void reducirStock(Long id, Integer cantidad) {
        Producto producto = buscarPorId(id);
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock Insuficiente: Solo quedan " + producto.getStock() + " unidades de " + producto.getNombre());
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    @Transactional
    public void reponerStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setStock(producto.getStock() + cantidad); 
        productoRepository.save(producto);
    }

    // Eliminar
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Error: No se puede eliminar un producto que no existe.");
        }
        productoRepository.deleteById(id);
    }
}
