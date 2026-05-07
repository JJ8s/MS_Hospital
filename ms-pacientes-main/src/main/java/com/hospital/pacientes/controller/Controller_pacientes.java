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





@RestController
@RequestMapping("/api/pacientes")
public class Controller_pacientes {

    @Autowired
    private Service_pacientes service;

    @GetMapping
    public List<Model_pacientes> Listar(){
        return service.obtenerTodos();
    }

    @PostMapping
    public ResponseEntity<Model_pacientes> crear(@Valid @RequestBody Model_pacientes pacientes){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(pacientes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model_pacientes> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
    @GetMapping("/rut/{rut}")
    public ResponseEntity<Model_pacientes> buscarPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.obtenerPorRut(rut));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Model_pacientes> actualizar(@PathVariable long id, @RequestBody Model_pacientes pacientes) {
        return ResponseEntity.ok(service.actualizar(id, pacientes));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
