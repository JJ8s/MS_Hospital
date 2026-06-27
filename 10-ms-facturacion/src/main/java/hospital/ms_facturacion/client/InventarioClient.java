package hospital.ms_facturacion.client;


import hospital.ms_facturacion.dto.ProductoDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;


@FeignClient(name = "ms-inventario")
public interface InventarioClient {
    @GetMapping("/api/productos/{id}")
    ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable("id") Long id);
}
