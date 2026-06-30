package com.hospital.ms_recetas.controller;

import com.hospital.ms_recetas.dto.request.RecetaRequestDTO;
import com.hospital.ms_recetas.dto.request.RecetaUpdateDTO;
import com.hospital.ms_recetas.dto.response.RecetaResponseDTO;
import com.hospital.ms_recetas.exception.ErrorResponse;
import com.hospital.ms_recetas.service.RecetaService;
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
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@Tag(name = "Recetas", description = "Operaciones relacionadas con recetas medicas y prescripciones")
@SecurityRequirement(name = "bearerAuth")
public class RecetaController {

    private final RecetaService recetaService;

    @Operation(summary = "Listar recetas", description = "Retorna el historial completo de recetas emitidas.")
    @ApiResponse(responseCode = "200", description = "Recetas recuperadas correctamente")
    @GetMapping
    public ResponseEntity<List<RecetaResponseDTO>> listar() {
        return ResponseEntity.ok(recetaService.listarTodas());
    }

    @Operation(summary = "Emitir receta", description = "Registra una receta y descuenta stock desde ms-inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Receta emitida correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Error al comunicar con inventario",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RecetaResponseDTO> crear(@Valid @RequestBody RecetaRequestDTO request) {
        return new ResponseEntity<>(recetaService.guardar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar receta por ID", description = "Obtiene una receta mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Receta encontrada"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar recetas por paciente", description = "Retorna las recetas asociadas a un paciente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recetas del paciente recuperadas correctamente"),
            @ApiResponse(responseCode = "404", description = "Paciente sin recetas registradas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<RecetaResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(recetaService.obtenerPorPaciente(pacienteId));
    }

    @Operation(summary = "Actualizar indicaciones", description = "Actualiza indicaciones y doctor responsable de una receta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Receta actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecetaUpdateDTO request) {
        return ResponseEntity.ok(recetaService.actualizarIndicaciones(id, request));
    }

    @Operation(summary = "Anular receta", description = "Elimina una receta y repone stock en ms-inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Receta anulada correctamente"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Error al comunicar con inventario",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
