package hospital.ms_auth.model;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "ms-user")
public interface AuthCliente {
    // Asegúrate de que el RequestParam sea "autoemail"
    @GetMapping("/api/users/search")
    Map<String, Object> getUserByAutoEmail(@RequestParam("autoemail") String email);
}
    
    
