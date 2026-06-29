package hospital.ms_auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hospital.ms_auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        // Cambiamos "username" por "email" para coincidir con la identidad autoEmail de ms-user
        String email = credentials.get("email");  // dentro de postman se utiliza "email" y no "auto_email" o "autoemail"
        // Se mantiene "password" como estándar de entrada, aunque internamente sea "contrasena"
        String contrasena = credentials.get("contrasena");

        // El AuthService ahora debe buscar al usuario por su correo institucional
        String token = authService.login(email, contrasena);
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}