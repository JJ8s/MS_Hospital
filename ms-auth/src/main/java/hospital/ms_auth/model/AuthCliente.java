package hospital.ms_auth.model;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "ms-user")
public interface AuthCliente {

    // Cambia "/api/users/search" por la ruta que tengas en ms-user para buscar por username
    @GetMapping("/api/users/search")
    Map<String, Object> getUserByUsername(@RequestParam("username") String username);
    
    
}
