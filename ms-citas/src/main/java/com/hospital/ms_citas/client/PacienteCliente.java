package com.hospital.ms_citas.client;

import com.hospital.ms_citas.config.FeignConfig;
import com.hospital.ms_citas.dto.external.PacienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pacientes", configuration = FeignConfig.class)
public interface PacienteCliente {

    @GetMapping("/api/pacientes/{id}")
    PacienteResponseDTO obtenerPacientePorId(@PathVariable("id") Long id);
}
