package hospital.ms_auth.model;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

// 🌟 CORRECCIÓN LOCAL: Añadimos la URL exacta donde corre ms-user
@FeignClient(name = "ms-user")
public interface AuthCliente {

    @GetMapping("/api/users/internal/auth-data")
    Map<String, Object> getUserByAutoEmail(@RequestParam("email") String email);
}