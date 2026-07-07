package com.hospital.ms_urgencia.client;

import com.hospital.ms_urgencia.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pacientes", configuration = FeignConfig.class)
public interface PacienteCliente {

    @GetMapping("/api/pacientes/{id}")
    Object obtenerPacientePorId(@PathVariable("id") Long id);
}
