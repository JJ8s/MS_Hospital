package com.hospital.ms_citas.controller;

import com.hospital.ms_citas.dto.request.CitaRequestDTO;
import com.hospital.ms_citas.model.EstadoCita;
import com.hospital.ms_citas.service.Service_citas;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/citas"})
@Tag(
   name = "Citas",
   description = "Operaciones relacionadas con la gestión de citas médicas"
)
   public class Controller_citas {
      private final Service_citas service;

      public Controller_citas(Service_citas service) {
         this.service = service;
      }

      @Operation(
         summary = "Listar todas las citas"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Lista obtenida correctamente"
   )})
      @GetMapping
      public ResponseEntity listarCitas() {
         return ResponseEntity.ok(this.service.listarTodas());
      }

      @Operation(
         summary = "Buscar cita por ID"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Cita encontrada"
   ), @ApiResponse(
      responseCode = "404",
      description = "Cita no encontrada"
   )})
      @GetMapping({"/{id}"})
      public ResponseEntity buscarPorId(@PathVariable Long id) {
         return ResponseEntity.ok(this.service.obtenerPorId(id));
      }

      @Operation(
         summary = "Buscar citas por paciente"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Citas encontradas"
   ), @ApiResponse(
      responseCode = "404",
      description = "No se encontraron citas"
   )})
      @GetMapping({"/paciente/{pacienteId}"})
      public ResponseEntity buscarPorPaciente(@PathVariable Long pacienteId) {
         return ResponseEntity.ok(this.service.obtenerPorPaciente(pacienteId));
      }

      @Operation(
         summary = "Buscar citas por médico"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Citas encontradas"
   ), @ApiResponse(
      responseCode = "404",
      description = "No se encontraron citas"
   )})
      @GetMapping({"/medico/{medicoId}"})
      public ResponseEntity buscarPorMedico(@PathVariable Long medicoId) {
         return ResponseEntity.ok(this.service.obtenerPorMedico(medicoId));
      }

      @Operation(
         summary = "Buscar citas por estado"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Citas encontradas"
   ), @ApiResponse(
      responseCode = "404",
      description = "No se encontraron citas"
   )})
      @GetMapping({"/estado/{estado}"})
      public ResponseEntity buscarPorEstado(@PathVariable EstadoCita estado) {
         return ResponseEntity.ok(this.service.obtenerPorEstado(estado));
      }

      @Operation(
         summary = "Agendar una nueva cita"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "201",
      description = "Cita agendada correctamente"
   ), @ApiResponse(
      responseCode = "400",
      description = "Datos inválidos"
   ), @ApiResponse(
      responseCode = "404",
      description = "Médico o paciente no encontrado"
   ), @ApiResponse(
      responseCode = "409",
      description = "Horario no disponible"
   )})
      @PostMapping
      public ResponseEntity agendarCita(@Valid @RequestBody CitaRequestDTO citaDTO) {
         return ResponseEntity.status(HttpStatus.CREATED).body(this.service.agendarCita(citaDTO));
      }

      @Operation(
         summary = "Actualizar una cita"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Cita actualizada"
   ), @ApiResponse(
      responseCode = "404",
      description = "Cita no encontrada"
   )})
      @PutMapping({"/{id}"})
      public ResponseEntity actualizar(@PathVariable Long id, @Valid @RequestBody CitaRequestDTO citaDTO) {
         return ResponseEntity.ok(this.service.actualizar(id, citaDTO));
      }

      @Operation(
         summary = "Cancelar una cita"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "200",
      description = "Cita cancelada"
   ), @ApiResponse(
      responseCode = "404",
      description = "Cita no encontrada"
   )})
      @PatchMapping({"/{id}/cancelar"})
      public ResponseEntity cancelarCita(@PathVariable Long id) {
         return ResponseEntity.ok(this.service.cancelarCita(id));
      }

      @Operation(
         summary = "Eliminar una cita"
      )
      @ApiResponses({@ApiResponse(
      responseCode = "204",
      description = "Cita eliminada"
   ), @ApiResponse(
      responseCode = "404",
      description = "Cita no encontrada"
   )})
      @DeleteMapping({"/{id}"})
      public ResponseEntity eliminarCita(@PathVariable Long id) {
         this.service.eliminarFisicamente(id);
         return ResponseEntity.noContent().build();
      }
   }
