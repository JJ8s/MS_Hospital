package com.hospital.ms_citas.controller;
import com.hospital.ms_citas.model.Model_citas;
import com.hospital.ms_citas.service.Service_citas;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/citas")
public class Controller_citas {
    
    @Autowired
    private Service_citas service_citas;

    @GetMapping
    public ResponseEntity<List<Model_citas>> ListarCitas(){
        return ResponseEntity.ok(service_citas.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model_citas> buscarPorId(@PathVariable Long id) {
        return service_citas.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        
    }
    
    @PostMapping
    public ResponseEntity<Model_citas> agendarCita(@Valid @RequestBody Model_citas cita){
        Model_citas nuevaCita = service_citas.agendarCita(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        service_citas.cancelarCita(id);
        return ResponseEntity.noContent().build();    
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id){
        service_citas.eliminarFisicamente(id);
        return ResponseEntity.noContent().build();
    }
}
