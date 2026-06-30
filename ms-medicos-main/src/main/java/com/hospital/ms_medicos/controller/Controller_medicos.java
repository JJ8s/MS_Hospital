package com.hospital.ms_medicos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.ms_medicos.dto.request.MedicoRequestDTO;
import com.hospital.ms_medicos.dto.response.MedicoResponseDTO;
import com.hospital.ms_medicos.service.Service_medicos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Médicos", description = "Operaciones relacionadas con la gestión de médicos")
public class Controller_medicos {

    private final Service_medicos service_medicos;

    public Controller_medicos(Service_medicos service_medicos) {
        this.service_medicos = service_medicos;
    }

    @Operation(summary = "Listar todos los médicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> listarMedicos() {
        return ResponseEntity.ok(service_medicos.obtenerTodos());
    }

    @Operation(summary = "Crear un nuevo médico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médico creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un médico con ese RUT")
    })
    @PostMapping
    public ResponseEntity<MedicoResponseDTO> crearMedico(
            @Valid @RequestBody MedicoRequestDTO medicoDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service_medicos.guardar(medicoDTO));
    }

    @Operation(summary = "Buscar médico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médico encontrado"),
            @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service_medicos.obtenerPorId(id));
    }

    @Operation(summary = "Buscar médico por RUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médico encontrado"),
            @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<MedicoResponseDTO> buscarPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(service_medicos.obtenerPorRut(rut));
    }

    @Operation(summary = "Buscar médicos por especialidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médicos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron médicos")
    })
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<MedicoResponseDTO>> buscarPorEspecialidad(
            @PathVariable String especialidad) {

        return ResponseEntity.ok(service_medicos.obtenerPorEspecialidad(especialidad));
    }

    @Operation(summary = "Actualizar un médico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médico actualizado"),
            @ApiResponse(responseCode = "404", description = "Médico no encontrado"),
            @ApiResponse(responseCode = "409", description = "El RUT pertenece a otro médico")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MedicoRequestDTO medicoDTO) {

        return ResponseEntity.ok(service_medicos.actualizar(id, medicoDTO));
    }

    @Operation(summary = "Eliminar un médico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Médico eliminado"),
            @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service_medicos.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}