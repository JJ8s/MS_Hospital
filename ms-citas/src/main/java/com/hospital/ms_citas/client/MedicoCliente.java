package com.hospital.ms_citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="ms-medicos")
public interface MedicoCliente {
    
    @GetMapping("api/medicos/{id}")
    Object obtenerMedicoPorId(@PathVariable("id") Long id);
}
