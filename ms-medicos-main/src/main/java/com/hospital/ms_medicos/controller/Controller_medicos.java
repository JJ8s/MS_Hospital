package com.hospital.ms_medicos.controller;



import com.hospital.ms_medicos.model.Model_medicos;
import com.hospital.ms_medicos.service.Service_medicos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/medicos")
public class Controller_medicos {
    @Autowired
    private Service_medicos service_medicos;

    
    @Operation(summary="Listar todos los medicos")
    @GetMapping
    public List<Model_medicos> listarMedicos(){
        return service_medicos.obtenerTodos();
    }
    
    @Operation(summary = "Crear un nuevo medico (solo admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Medico creado"),
        @ApiResponse(responseCode = "409", description = "Conflicto: Rut duplicado"),
        @ApiResponse(responseCode = "400",description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<Model_medicos> crearMedico(@Valid @RequestBody Model_medicos medicos){
        return ResponseEntity.status(HttpStatus.CREATED).body(service_medicos.guardar(medicos));
    }

    @Operation(summary = "Buscar por id")
    @GetMapping("/{id}")
    public ResponseEntity<Model_medicos> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(service_medicos.obtenerPorId(id));
    }

    @Operation(summary = "Buscar por rut")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<Model_medicos> buscarPorRut(@PathVariable String rut){
        return ResponseEntity.ok(service_medicos.obtenerPorRut(rut));
    }

    @Operation(summary = "Buscar por especialidad")
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<Model_medicos>> buscarPorEspecialidad(@PathVariable String especialidad){
        return ResponseEntity.ok(service_medicos.obtenerPorEspecialidad(especialidad));
    }
    @Operation(summary = "Actualizar un medico existente (solo admin)")
    @PutMapping("/{id}")
    public ResponseEntity<Model_medicos> actualizar(@PathVariable Long id, @Valid @RequestBody Model_medicos medicos){
        return ResponseEntity.ok(service_medicos.actualizar(id, medicos));
    }
    
    @Operation(summary = "Eliminar un medico (solo admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service_medicos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
