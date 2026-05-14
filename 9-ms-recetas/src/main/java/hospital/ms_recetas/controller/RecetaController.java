package hospital.ms_recetas.controller;

import hospital.ms_recetas.model.Receta;
import hospital.ms_recetas.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    // 1. Listar todas las recetas 
    @GetMapping
    public ResponseEntity<List<Receta>> listar() {
        return ResponseEntity.ok(recetaService.listarTodas());
    }

    // 2. Crear una receta nueva 
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Receta receta) {
        try {
            Receta nuevaReceta = recetaService.guardar(receta);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "¡Receta emitida con éxito! Stock actualizado en inventario.");
            respuesta.put("receta", nuevaReceta);
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al emitir receta: " + e.getMessage());
        }
    }

    // 3. Buscar receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Receta receta = recetaService.obtenerPorId(id);
            return ResponseEntity.ok(receta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error: No se encontró la receta #" + id);
        }
    }

    // 4. Buscar todas las recetas de un Paciente 
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> listarPorPaciente(@PathVariable Long pacienteId) {
        List<Receta> recetas = recetaService.obtenerPorPaciente(pacienteId);
        if (recetas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("El paciente #" + pacienteId + " no tiene recetas registradas.");
        }
        return ResponseEntity.ok(recetas);
    }

    // 5. Eliminar o anular receta 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            recetaService.eliminar(id);
            return ResponseEntity.ok("La receta #" + id + " ha sido anulada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se pudo eliminar: " + e.getMessage());
        }
    }

    // 6. Actualizar indicaciones de la receta
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Receta recetaDetalles) {
        try {
            Receta recetaActualizada = recetaService.actualizarIndicaciones(id, recetaDetalles);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Indicaciones actualizadas correctamente.");
            respuesta.put("receta", recetaActualizada);
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error al actualizar: " + e.getMessage());
        }
    }
}