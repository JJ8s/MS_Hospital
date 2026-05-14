package hospital.ms_facturacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inventario")
public interface InventarioClient {
    @GetMapping("/api/productos/{id}")
    Object obtenerPorId(@PathVariable("id") Long id);
}
