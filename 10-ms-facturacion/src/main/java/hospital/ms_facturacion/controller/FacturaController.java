package hospital.ms_facturacion.controller;

import hospital.ms_facturacion.model.Factura;
import hospital.ms_facturacion.repository.FacturaRepository;
import hospital.ms_facturacion.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private FacturaRepository facturaRepository;

    // 1. Listar todas las facturas
    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaService.listarTodas());
    }

    // 2. Crear una nueva factura
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Factura factura) {
        try {
            Factura nuevaFactura = facturaService.crearFactura(factura);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Factura generada exitosamente. Pendiente de pago.");
            respuesta.put("factura", nuevaFactura);
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al generar factura: " + e.getMessage());
        }
    }

    // 3. Pagar una factura
    @PostMapping("/{id}/pagar")
    public ResponseEntity<?> marcarComoPagada(@PathVariable Long id) {
        return facturaRepository.findById(id).map(factura -> {
            if ("PAGADA".equals(factura.getEstado())) {
                return ResponseEntity.badRequest().body("La factura #" + id + " ya se encuentra pagada.");
            }
            factura.setEstado("PAGADA");
            Factura facturaActualizada = facturaRepository.save(factura);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "¡Pago procesado con éxito!");
            respuesta.put("factura", facturaActualizada);
            return ResponseEntity.ok(respuesta);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No se encontró la factura #" + id + " para procesar el pago."));
    }

    // 4. Buscar factura por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return facturaRepository.findById(id)
                .map(factura -> ResponseEntity.ok((Object) factura))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("La factura con ID " + id + " no existe."));
    }

    // 5. Actualizar factura
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Factura facturaDetalles) {
        return facturaRepository.findById(id).map(factura -> {
            factura.setRecetaId(facturaDetalles.getRecetaId());
            factura.setPacienteId(facturaDetalles.getPacienteId());
            factura.setMontoTotal(facturaDetalles.getMontoTotal());
            factura.setEstado(facturaDetalles.getEstado());
            Factura actualizada = facturaRepository.save(factura);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Factura #" + id + " actualizada correctamente.");
            respuesta.put("factura", actualizada);
            return ResponseEntity.ok().body((Object) respuesta); 
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se puede actualizar: Factura #" + id + " no encontrada."));
    }

    // 6. Eliminar factura
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        return facturaRepository.findById(id).map(factura -> {
            facturaRepository.delete(factura);
            return ResponseEntity.ok("La factura #" + id + " ha sido eliminada del sistema.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: La factura que intenta eliminar no existe."));
    }
}