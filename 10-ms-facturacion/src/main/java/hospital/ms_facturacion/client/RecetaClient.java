package hospital.ms_facturacion.client;

import hospital.ms_facturacion.dto.RecetaDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;


@FeignClient(name = "ms-recetas")
public interface RecetaClient {
    @GetMapping("/api/recetas/{id}")
    ResponseEntity<RecetaDTO> obtenerPorId(@PathVariable("id") Long id);
}

