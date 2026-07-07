package com.hospital.ms_notificaciones.controller;

import com.hospital.ms_notificaciones.dto.request.NotificacionRequestDTO;
import com.hospital.ms_notificaciones.dto.response.NotificacionResponseDTO;
import com.hospital.ms_notificaciones.exception.ErrorResponse;
import com.hospital.ms_notificaciones.service.NotificacionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Operaciones relacionadas con notificaciones hospitalarias")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Listar notificaciones", description = "Obtiene todas las notificaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Notificaciones recuperadas correctamente")
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @Operation(summary = "Crear notificacion", description = "Registra una nueva notificacion en estado PENDIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificacion creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO request) {
        return new ResponseEntity<>(notificacionService.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar notificacion por ID", description = "Obtiene una notificacion mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificacion encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificacion no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @Operation(summary = "Listar por destinatario", description = "Obtiene notificaciones asociadas a un destinatario.")
    @ApiResponse(responseCode = "200", description = "Notificaciones del destinatario recuperadas correctamente")
    @GetMapping("/destinatario/{destinatarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorDestinatario(@PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.listarPorDestinatario(destinatarioId));
    }

    @Operation(summary = "Listar por estado", description = "Obtiene notificaciones por estado.")
    @ApiResponse(responseCode = "200", description = "Notificaciones filtradas correctamente")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }

    @Operation(summary = "Marcar como enviada", description = "Cambia una notificacion a estado ENVIADA.")
    @ApiResponse(responseCode = "200", description = "Notificacion marcada como enviada")
    @PostMapping("/{id}/enviar")
    public ResponseEntity<NotificacionResponseDTO> marcarComoEnviada(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    @Operation(summary = "Marcar como leida", description = "Cambia una notificacion enviada a estado LEIDA.")
    @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida")
    @PostMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @Operation(summary = "Eliminar notificacion", description = "Elimina una notificacion existente.")
    @ApiResponse(responseCode = "204", description = "Notificacion eliminada correctamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
