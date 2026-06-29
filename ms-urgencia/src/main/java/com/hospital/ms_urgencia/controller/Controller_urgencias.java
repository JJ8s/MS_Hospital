package com.hospital.ms_urgencia.controller;

import com.hospital.ms_urgencia.dto.request.UrgenciaRequestDTO;
import com.hospital.ms_urgencia.dto.response.UrgenciaResponseDTO;
import com.hospital.ms_urgencia.service.Service_urgencia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urgencias")
@Tag(name = "Urgencias", description = "Operaciones relacionadas con la gestión de urgencias médicas")
public class Controller_urgencias {

    private final Service_urgencia service;

    public Controller_urgencias(Service_urgencia service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las urgencias")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")})
    @GetMapping
    public ResponseEntity<List<UrgenciaResponseDTO>> listar() {
        return ResponseEntity.ok(service.obtenerTodasLasUrgencias());
    }

    @Operation(summary = "Buscar un registro de urgencia por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Urgencia encontrada"),
        @ApiResponse(responseCode = "404", description = "Urgencia no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UrgenciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Registrar una nueva urgencia")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Urgencia creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Paciente referenciado no encontrado")
    })
    @PostMapping
    public ResponseEntity<UrgenciaResponseDTO> crear(@Valid @RequestBody UrgenciaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(summary = "Actualizar el nivel de triage de una urgencia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nivel de triage actualizado"),
        @ApiResponse(responseCode = "400", description = "Nivel de triage inválido"),
        @ApiResponse(responseCode = "404", description = "Urgencia no encontrada")
    })
    @PatchMapping("/{id}/triage")
    public ResponseEntity<UrgenciaResponseDTO> updateTriage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // Extraemos el nivel del JSON del cuerpo para evitar errores de formato
        String nivel = body.get("nivel");
        if (nivel == null || nivel.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.actualizarTriage(id, nivel));
    }

    @Operation(summary = "Eliminar un registro de urgencia por id (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Urgencia eliminada"),
        @ApiResponse(responseCode = "404", description = "Urgencia no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}