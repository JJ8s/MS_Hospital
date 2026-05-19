package com.hospital.pacientes.controller;
import com.hospital.pacientes.model.Model_pacientes;
import com.hospital.pacientes.service.Service_pacientes;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;




@RestController
@RequestMapping("/api/pacientes")
public class Controller_pacientes {

    @Autowired
    private Service_pacientes service;

    @Operation(summary = "Listar todos los pacientes")
    @GetMapping
    public List<Model_pacientes> Listar(){
        return service.obtenerTodos();
    }

    @Operation(summary = "Crear un nuevo paciente (solo ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Conflicto: Rut duplicado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<Model_pacientes> crear(@Valid @RequestBody Model_pacientes pacientes){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(pacientes));
    }

    @Operation(summary = "Buscar paciente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Model_pacientes> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
    @Operation(summary = "Buscar paciente por RUT")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<Model_pacientes> buscarPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.obtenerPorRut(rut));
    }
    
    @Operation(summary = "Actualizar paciente por ID (solo ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<Model_pacientes> actualizar(@PathVariable long id, @RequestBody Model_pacientes pacientes) {
        return ResponseEntity.ok(service.actualizar(id, pacientes));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar paciente por ID (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
