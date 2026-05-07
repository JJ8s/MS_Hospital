package com.hospital.ms_medicos.controller;




import com.hospital.ms_medicos.model.Model_medicos;
import com.hospital.ms_medicos.service.Service_medicos;

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
import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/api/medicos")
public class Controller_medicos {
    @Autowired
    private Service_medicos service_medicos;

    @GetMapping
    public List<Model_medicos> listarMedicos(){
        return service_medicos.obtenerTodos();
    }
    
    @PostMapping
    public ResponseEntity<Model_medicos> crearMedico(@Valid @RequestBody Model_medicos medicos){
        return ResponseEntity.status(HttpStatus.CREATED).body(service_medicos.guardar(medicos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model_medicos> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(service_medicos.obtenerPorId(id));
    }
    @GetMapping("/rut/{rut}")
    public ResponseEntity<Model_medicos> buscarPorRut(@PathVariable String rut){
        return ResponseEntity.ok(service_medicos.obtenerPorRut(rut));
    }
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<Model_medicos>> buscarPorEspecialidad(@PathVariable String especialidad){
        return ResponseEntity.ok(service_medicos.obtenerPorEspecialidad(especialidad));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Model_medicos> actualizar(@PathVariable Long id, @Valid @RequestBody Model_medicos medicos){
        return ResponseEntity.ok(service_medicos.actualizar(id, medicos));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service_medicos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
