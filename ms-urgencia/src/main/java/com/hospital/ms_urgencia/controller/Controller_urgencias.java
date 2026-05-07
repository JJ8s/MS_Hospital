package com.hospital.ms_urgencia.controller;
import com.hospital.ms_urgencia.model.Model_urgencias;

import com.hospital.ms_urgencia.service.Service_urgencia;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urgencias")
public class Controller_urgencias {
    @Autowired
    private Service_urgencia service_urgencia;

    @GetMapping 
    public List<Model_urgencias> listar(){
        return service_urgencia.obtenerTodasLasUrgencias();
    }
    @PostMapping
    public ResponseEntity<Model_urgencias> crear(@Valid @RequestBody Model_urgencias urgencias){
        return new ResponseEntity<>(service_urgencia.guardar(urgencias), HttpStatus.CREATED);
    }
    @PatchMapping("/{id}/triage")
        public ResponseEntity<Model_urgencias> updateTriage(@PathVariable Long id, @RequestBody Map<String, String> body) {
            // Extraemos el nivel del JSON del cuerpo para evitar errores de formato
            String nivel = body.get("nivel");
            return ResponseEntity.ok(service_urgencia.actualizarTriage(id, nivel));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        service_urgencia.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
