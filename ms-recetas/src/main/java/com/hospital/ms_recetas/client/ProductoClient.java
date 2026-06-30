package com.hospital.ms_recetas.client;

import com.hospital.ms_recetas.config.FeignConfig;
import com.hospital.ms_recetas.dto.request.AjusteStockRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario", configuration = FeignConfig.class)
public interface ProductoClient {

    @PutMapping("/api/productos/{id}/reducir-stock")
    void reducirStock(@PathVariable("id") Long id, @RequestBody AjusteStockRequestDTO request);

    @PutMapping("/api/productos/{id}/reponer-stock")
    void reponerStock(@PathVariable("id") Long id, @RequestBody AjusteStockRequestDTO request);

    @GetMapping("/api/productos/{id}")
    Object obtenerPorId(@PathVariable("id") Long id);
}
