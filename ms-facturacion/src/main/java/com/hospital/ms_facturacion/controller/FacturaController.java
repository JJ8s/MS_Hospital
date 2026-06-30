package com.hospital.ms_facturacion.controller;

import com.hospital.ms_facturacion.dto.request.FacturaRequestDTO;
import com.hospital.ms_facturacion.dto.request.FacturaUpdateDTO;
import com.hospital.ms_facturacion.dto.response.FacturaResponseDTO;
import com.hospital.ms_facturacion.exception.ErrorResponse;
import com.hospital.ms_facturacion.service.FacturaService;
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
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturacion", description = "Operaciones relacionadas con cobros y facturas hospitalarias")
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {

    private final FacturaService facturaService;

    @Operation(summary = "Listar facturas", description = "Obtiene el historial completo de facturas generadas.")
    @ApiResponse(responseCode = "200", description = "Facturas recuperadas correctamente")
    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> listar() {
        return ResponseEntity.ok(facturaService.listarTodas());
    }

    @Operation(summary = "Generar factura", description = "Crea una factura asociada a una receta medica.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Factura generada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Factura duplicada para la receta",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Error al comunicar con recetas o inventario",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crear(@Valid @RequestBody FacturaRequestDTO request) {
        return new ResponseEntity<>(facturaService.crearFactura(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Procesar pago", description = "Cambia el estado de una factura pendiente a PAGADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Estado de factura invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/pagar")
    public ResponseEntity<FacturaResponseDTO> marcarComoPagada(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.pagarFactura(id));
    }

    @Operation(summary = "Obtener factura por ID", description = "Busca una factura mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Factura encontrada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar facturas por paciente", description = "Obtiene las facturas asociadas a un paciente.")
    @ApiResponse(responseCode = "200", description = "Facturas del paciente recuperadas correctamente")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<FacturaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(facturaService.obtenerPorPaciente(pacienteId));
    }

    @Operation(summary = "Actualizar factura", description = "Permite modificar el costo de servicio de una factura pendiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Factura actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Estado de factura invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FacturaUpdateDTO request) {
        return ResponseEntity.ok(facturaService.actualizarFactura(id, request));
    }

    @Operation(summary = "Eliminar factura", description = "Elimina una factura si no se encuentra pagada.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Factura eliminada correctamente"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar una factura pagada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}
