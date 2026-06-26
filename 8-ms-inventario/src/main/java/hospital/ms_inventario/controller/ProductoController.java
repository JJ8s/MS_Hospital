package hospital.ms_inventario.controller;

import hospital.ms_inventario.model.Producto;
import hospital.ms_inventario.service.ProductoService;
import hospital.ms_inventario.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Producto Controller", description = "Gestión integral del inventario hospitalario")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listar inventario", description = "Obtiene todos los productos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada")
    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    @Operation(summary = "Buscar por ID", description = "Retorna un producto o lanza error si no existe.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "ID inexistente", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @Operation(summary = "Registrar producto", description = "Crea un producto validando integridad de datos.")
    @ApiResponse(responseCode = "201", description = "Creado exitosamente")
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return new ResponseEntity<>(productoService.guardar(producto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar producto", description = "Modifica un producto existente.")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto detalles) {
        return ResponseEntity.ok(productoService.actualizar(id, detalles));
    }

    @Operation(summary = "Eliminar producto", description = "Borrado lógico/físico tras verificar existencia.")
    @ApiResponse(responseCode = "204", description = "Eliminado correctamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

      @Operation(
        summary = "Reducir stock de un producto", 
        description = "Descuenta una cantidad específica del inventario. Lanza error si el stock es insuficiente o el producto no existe."
    )
    @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Stock insuficiente para la operación", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<String> reducirStock(
            @PathVariable Long id, 
            @RequestParam Integer cantidad) {
        
        
        productoService.reducirStock(id, cantidad);
        return ResponseEntity.ok("Operación exitosa: Se descontaron " + cantidad + " unidades del producto #" + id);
    }

    @Operation(
        summary = "Reponer stock de un producto", 
        description = "Incrementa la cantidad de existencias de un insumo médico en el inventario."
    )
    @ApiResponse(responseCode = "200", description = "Reposición exitosa")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}/reponer-stock")
    public ResponseEntity<String> reponerStock(
            @PathVariable Long id, 
            @RequestParam Integer cantidad) {
        
        productoService.reponerStock(id, cantidad);
        return ResponseEntity.ok("Stock repuesto: +" + cantidad + " unidades para el producto #" + id);
    }
}