package com.hospital.ms_citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "ms-pacientes")
public interface PacienteCliente {
    
    @GetMapping("/api/pacientes/{id}")
    Object obtenerPacientePorId(@PathVariable("id") Long id);
}
