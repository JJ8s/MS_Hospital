package com.hospital.ms_facturacion.client;

import com.hospital.ms_facturacion.config.FeignConfig;
import com.hospital.ms_facturacion.dto.RecetaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-recetas", configuration = FeignConfig.class)
public interface RecetaClient {

    @GetMapping("/api/recetas/{id}")
    ResponseEntity<RecetaDTO> obtenerPorId(@PathVariable("id") Long id);
}
