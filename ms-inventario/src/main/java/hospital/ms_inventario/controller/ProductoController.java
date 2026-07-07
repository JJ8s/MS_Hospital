package hospital.ms_inventario.controller;

import hospital.ms_inventario.model.Producto;
import hospital.ms_inventario.repository.ProductoRepository;
import hospital.ms_inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.buscarPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Producto #" + id + " no encontrado.");
        }
    }

    
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return new ResponseEntity<>(productoService.guardar(producto), HttpStatus.CREATED);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Producto detalles) {
        try {
            Producto actualizado = productoService.actualizar(id, detalles);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Producto actualizado con éxito");
            respuesta.put("producto", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al actualizar: " + e.getMessage());
        }
    }

    
    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<String> reducirStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            productoService.reducirStock(id, cantidad);
            return ResponseEntity.ok("Operación exitosa: Se descontaron " + cantidad + " unidades del producto #" + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de inventario: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reponer-stock")
    public ResponseEntity<String> reponerStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        productoService.reponerStock(id, cantidad);
        return ResponseEntity.ok("Stock repuesto: +" + cantidad);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        return productoRepository.findById(id).map(producto -> {
            productoRepository.delete(producto);
            return ResponseEntity.ok("EXITO: El producto '" + producto.getNombre() + "' (ID: " + id + ") ha sido borrado del inventario.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: No se puede eliminar. El producto con ID " + id + " no existe en la base de datos."));
    }
}