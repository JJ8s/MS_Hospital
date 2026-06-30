package com.hospital.ms_inventario.controller;

import com.hospital.ms_inventario.dto.request.AjusteStockRequestDTO;
import com.hospital.ms_inventario.dto.request.ProductoRequestDTO;
import com.hospital.ms_inventario.dto.response.ProductoResponseDTO;
import com.hospital.ms_inventario.exception.ErrorResponse;
import com.hospital.ms_inventario.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Operaciones relacionadas con el inventario hospitalario")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar productos", description = "Obtiene todos los productos registrados en inventario.")
    @ApiResponse(responseCode = "200", description = "Productos recuperados correctamente")
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @Operation(summary = "Buscar producto por ID", description = "Retorna un producto segun su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @Operation(summary = "Registrar producto", description = "Crea un producto validando nombre, lote, precio, vencimiento y stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Producto o lote duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
        return new ResponseEntity<>(productoService.guardar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Producto o lote duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto tras verificar que exista.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reducir stock", description = "Descuenta unidades del inventario de un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock reducido correctamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Stock insuficiente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/reducir-stock")
    public ResponseEntity<ProductoResponseDTO> reducirStock(
            @PathVariable Long id,
            @Valid @RequestBody AjusteStockRequestDTO request) {
        return ResponseEntity.ok(productoService.reducirStock(id, request.getCantidad()));
    }

    @Operation(summary = "Reponer stock", description = "Incrementa unidades disponibles de un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock repuesto correctamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}/reponer-stock")
    public ResponseEntity<ProductoResponseDTO> reponerStock(
            @PathVariable Long id,
            @Valid @RequestBody AjusteStockRequestDTO request) {
        return ResponseEntity.ok(productoService.reponerStock(id, request.getCantidad()));
    }
}
