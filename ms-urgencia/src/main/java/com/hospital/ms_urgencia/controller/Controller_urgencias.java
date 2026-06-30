package com.hospital.ms_urgencia.controller;

import com.hospital.ms_urgencia.dto.request.TriageUpdateDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/urgencias")
@Tag(name = "Urgencias", description = "Operaciones relacionadas con la gestión de urgencias médicas")
public class Controller_urgencias {

    private final Service_urgencia service;

    public Controller_urgencias(Service_urgencia service) {
        this.service = service;
    }

    @Operation(summary = "Listar urgencias", description = "Obtiene todos los registros de urgencia.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Token no autorizado")
    })
    @GetMapping
    public ResponseEntity<List<UrgenciaResponseDTO>> listar() {
        return ResponseEntity.ok(service.obtenerTodasLasUrgencias());
    }

    @Operation(summary = "Buscar urgencia por ID", description = "Obtiene un registro de urgencia según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Urgencia encontrada"),
            @ApiResponse(responseCode = "404", description = "Urgencia no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UrgenciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Listar urgencias por paciente", description = "Obtiene urgencias asociadas a un paciente registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<UrgenciaResponseDTO>> buscarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(service.obtenerPorPaciente(pacienteId));
    }

    @Operation(summary = "Registrar urgencia", description = "Crea un registro de urgencia validando previamente que el paciente exista en ms-pacientes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Urgencia creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente referenciado no encontrado")
    })
    @PostMapping
    public ResponseEntity<UrgenciaResponseDTO> crear(@Valid @RequestBody UrgenciaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @Operation(summary = "Actualizar nivel de triage", description = "Actualiza el nivel de triage de una urgencia existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nivel de triage actualizado"),
            @ApiResponse(responseCode = "400", description = "Nivel de triage inválido"),
            @ApiResponse(responseCode = "404", description = "Urgencia no encontrada")
    })
    @PatchMapping("/{id}/triage")
    public ResponseEntity<UrgenciaResponseDTO> actualizarTriage(
            @PathVariable Long id,
            @Valid @RequestBody TriageUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.actualizarTriage(id, dto.getNivel()));
    }

    @Operation(summary = "Cerrar urgencia", description = "Marca una urgencia como ATENDIDA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Urgencia cerrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Urgencia no encontrada")
    })
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<UrgenciaResponseDTO> cerrarUrgencia(@PathVariable Long id) {
        return ResponseEntity.ok(service.cerrarUrgencia(id));
    }

    @Operation(summary = "Eliminar urgencia", description = "Elimina un registro de urgencia por ID. Requiere rol ADMIN.")
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
