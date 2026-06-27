package hospital.ms_facturacion.controller;

import hospital.ms_facturacion.model.Factura;
import hospital.ms_facturacion.service.FacturaService;
import hospital.ms_facturacion.exception.ErrorResponse;
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
@RequestMapping("/api/facturas")
@Tag(name = "Factura Controller", description = "API para la gestión de cobros y facturación hospitalaria")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Operation(summary = "Listar facturas", description = "Obtiene el historial completo de facturas generadas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @GetMapping
    public List<Factura> listar() {
        return facturaService.listarTodas();
    }

    @Operation(summary = "Generar nueva factura", description = "Crea un registro de cobro asociado a una receta médica.")
    @ApiResponse(responseCode = "201", description = "Factura generada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody Factura factura) {
        return new ResponseEntity<>(facturaService.crearFactura(factura), HttpStatus.CREATED);
    }

    @Operation(summary = "Procesar pago de factura", description = "Cambia el estado de una factura de 'PENDIENTE' a 'PAGADA'.")
    @ApiResponse(responseCode = "200", description = "Pago procesado con éxito")
    @ApiResponse(responseCode = "400", description = "La factura ya está pagada", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Factura no encontrada", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/pagar")
    public ResponseEntity<Factura> marcarComoPagada(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.pagarFactura(id));
    }

    @Operation(summary = "Obtener factura por ID", description = "Busca los detalles de cobro mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Factura encontrada")
    @ApiResponse(responseCode = "404", description = "ID de factura no existe", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Factura> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @Operation(summary = "Actualizar datos de factura", description = "Permite modificar montos o estados de una factura existente.")
    @ApiResponse(responseCode = "200", description = "Factura actualizada correctamente")
    @ApiResponse(responseCode = "404", description = "No se encontró la factura para actualizar", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<Factura> actualizar(@PathVariable Long id, @Valid @RequestBody Factura facturaDetalles) {
        return ResponseEntity.ok(facturaService.actualizarFactura(id, facturaDetalles));
    }

    @Operation(summary = "Eliminar factura", description = "Remueve permanentemente el registro de factura del sistema.")
    @ApiResponse(responseCode = "204", description = "Factura eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "La factura a eliminar no existe", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}