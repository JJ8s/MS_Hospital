package com.hospital.ms_citas.client;

import com.hospital.ms_citas.config.FeignConfig;
import com.hospital.ms_citas.dto.external.MedicoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-medicos", configuration = FeignConfig.class)
public interface MedicoCliente {

    @GetMapping("/api/medicos/{id}")
    MedicoResponseDTO obtenerMedicoPorId(@PathVariable("id") Long id);
}
