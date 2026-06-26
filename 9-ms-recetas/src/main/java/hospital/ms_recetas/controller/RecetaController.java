package hospital.ms_recetas.controller;

import hospital.ms_recetas.model.Receta;
import hospital.ms_recetas.service.RecetaService;
import hospital.ms_recetas.exception.ErrorResponse;
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
@RequestMapping("/api/recetas")
@Tag(name = "Receta Controller", description = "API para la gestión de recetas médicas y prescripciones hospitalarias")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @Operation(summary = "Listar todas las recetas", description = "Retorna el historial completo de recetas emitidas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @GetMapping
    public List<Receta> listar() {
        return recetaService.listarTodas();
    }

    @Operation(summary = "Emitir nueva receta", description = "Registra una prescripción médica validando stock en el inventario mediante comunicación REST.")
    @ApiResponse(responseCode = "201", description = "Receta emitida con éxito")
    @ApiResponse(responseCode = "400", description = "Error en los datos de la receta o stock insuficiente", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Receta> crear(@Valid @RequestBody Receta receta) {
        
        return new ResponseEntity<>(recetaService.guardar(receta), HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar receta por ID", description = "Obtiene los detalles de una receta específica mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Receta encontrada")
    @ApiResponse(responseCode = "404", description = "Receta no encontrada", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar recetas por Paciente", description = "Retorna todas las recetas asociadas a un ID de paciente específico.")
    @ApiResponse(responseCode = "200", description = "Lista de recetas del paciente recuperada")
    @ApiResponse(responseCode = "404", description = "El paciente no tiene recetas registradas", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Receta>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(recetaService.obtenerPorPaciente(pacienteId));
    }

    @Operation(summary = "Anular o eliminar receta", description = "Elimina físicamente una receta del sistema tras verificar su existencia.")
    @ApiResponse(responseCode = "204", description = "Receta anulada correctamente")
    @ApiResponse(responseCode = "404", description = "No se puede anular una receta inexistente", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar indicaciones", description = "Permite modificar las instrucciones médicas de una receta ya emitida.")
    @ApiResponse(responseCode = "200", description = "Indicaciones actualizadas exitosamente")
    @ApiResponse(responseCode = "404", description = "ID de receta no válido o inexistente", 
                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<Receta> actualizar(@PathVariable Long id, @Valid @RequestBody Receta recetaDetalles) {
        return ResponseEntity.ok(recetaService.actualizarIndicaciones(id, recetaDetalles));
    }
}