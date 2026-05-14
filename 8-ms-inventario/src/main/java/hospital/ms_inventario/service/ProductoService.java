package hospital.ms_inventario.service;

import hospital.ms_inventario.model.Producto;
import hospital.ms_inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Listar todos los productos
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Guardar un producto nuevo con validaciones de negocio
    @Transactional
    public Producto guardar(Producto producto) {
        // 1. Evitar duplicados por nombre
        if (productoRepository.existsByNombre(producto.getNombre())) {
            throw new RuntimeException("Error de Negocio: El producto '" + producto.getNombre() + "' ya está registrado.");
        }
        
        // 2. Evitar duplicados por lote (Muy importante en hospitales)
        if (productoRepository.findByLote(producto.getLote()).isPresent()) {
            throw new RuntimeException("Error de Trazabilidad: El lote '" + producto.getLote() + "' ya existe.");
        }

        return productoRepository.save(producto);
    }

    // Buscar por ID con mensaje profesional
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Error: No se encontró el producto con ID " + id));
    }

    // Actualizar producto (Lógica de actualización segura)
    @Transactional
    public Producto actualizar(Long id, Producto detallesActualizados) {
        Producto productoExistente = buscarPorId(id);
        
        // Actualizamos solo los campos necesarios manteniendo el ID original
        productoExistente.setNombre(detallesActualizados.getNombre());
        productoExistente.setLote(detallesActualizados.getLote());
        productoExistente.setFechaVencimiento(detallesActualizados.getFechaVencimiento());
        productoExistente.setPrecio(detallesActualizados.getPrecio());
        productoExistente.setStock(detallesActualizados.getStock());
        
        return productoRepository.save(productoExistente);
    }

    // Lógica especial: Ajustar Stock (para ms-recetas y ms-facturacion)
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
        producto.setStock(producto.getStock() + cantidad); // SUMAMOS
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
