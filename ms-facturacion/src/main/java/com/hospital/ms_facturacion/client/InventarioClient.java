package com.hospital.ms_facturacion.client;

import com.hospital.ms_facturacion.config.FeignConfig;
import com.hospital.ms_facturacion.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inventario", configuration = FeignConfig.class)
public interface InventarioClient {

    @GetMapping("/api/productos/{id}")
    ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable("id") Long id);
}
