package hospital.ms_facturacion.client;

import hospital.ms_facturacion.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.Map;


@FeignClient(name = "ms-recetas", configuration = FeignClientConfig.class)
public interface RecetaClient {

    @GetMapping("/api/recetas/{id}")
    ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable("id") Long id);
}

