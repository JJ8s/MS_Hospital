package hospital.ms_recetas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "ms-inventario") 
public interface ProductoClient {

    @PutMapping("/api/productos/{id}/reducir-stock")
    void reducirStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);

    @PutMapping("/api/productos/{id}/reponer-stock") 
    void reponerStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);
    
    @GetMapping("/api/productos/{id}")
    Object obtenerPorId(@PathVariable("id") Long id);

}
