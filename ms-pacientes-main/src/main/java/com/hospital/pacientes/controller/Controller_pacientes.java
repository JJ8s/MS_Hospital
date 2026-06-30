package com.hospital.pacientes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.pacientes.dto.request.PacienteRequestDTO;
import com.hospital.pacientes.dto.response.PacienteResponseDTO;
import com.hospital.pacientes.service.Service_pacientes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Operaciones relacionadas con la gestión de pacientes")
public class Controller_pacientes {

    private final Service_pacientes service;

    public Controller_pacientes(Service_pacientes service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los pacientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarPacientes() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Crear un nuevo paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paciente creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un paciente con ese RUT")
    })
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crearPaciente(
            @Valid @RequestBody PacienteRequestDTO pacienteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.guardar(pacienteDTO));
    }

    @Operation(summary = "Buscar paciente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Buscar paciente por RUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<PacienteResponseDTO> buscarPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.obtenerPorRut(rut));
    }

    @Operation(summary = "Buscar pacientes por previsión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron pacientes")
    })
    @GetMapping("/prevision/{prevision}")
    public ResponseEntity<List<PacienteResponseDTO>> buscarPorPrevision(
            @PathVariable String prevision) {
        return ResponseEntity.ok(service.obtenerPorPrevision(prevision));
    }

    @Operation(summary = "Actualizar un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente actualizado"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
            @ApiResponse(responseCode = "409", description = "El RUT pertenece a otro paciente")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO pacienteDTO) {
        return ResponseEntity.ok(service.actualizar(id, pacienteDTO));
    }

    @Operation(summary = "Eliminar un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paciente eliminado"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
