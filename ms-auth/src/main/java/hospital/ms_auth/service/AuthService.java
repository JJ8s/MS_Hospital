package hospital.ms_auth.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bouncycastle.math.ec.rfc8032.Ed25519.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hospital.ms_auth.exception.UnauthorizedException;
import hospital.ms_auth.model.AuthCliente;
import hospital.ms_auth.security.JwtUtils;



@Service
public class AuthService {
    
    @Autowired
    private AuthCliente authCliente;

    @Autowired
    private JwtUtils jwtUtils; // <--- Inyectamos la herramienta de tokens

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // <--- Inyectamos el encoder de SecurityConfig

    public String login(String username, String password) {
        // 1. Buscamos al usuario mediante OpenFeign
        Map<String, Object> user = authCliente.getUserByUsername(username);
        
        // 2. Validamos si el usuario existe
        if (user == null) {
            throw new UnauthorizedException("El usuario '" + username + "' no existe en el sistema.");
        }

        // 3. Extraemos la contraseña encriptada
        String encryptedPassword = (String) user.get("password");

        // 4. Comparamos con BCrypt
        if (!passwordEncoder.matches(password, encryptedPassword)) {
            throw new UnauthorizedException("La contraseña ingresada es incorrecta.");
        }

        // 5. Extraer TODOS los roles y meterlos en una Lista de Strings
        List<String> misRoles = new ArrayList<>();
        List<Map<String, Object>> rolesList = (List<Map<String, Object>>) user.get("roles");
        
        if (rolesList != null && !rolesList.isEmpty()) {
            for (Map<String, Object> rolMap : rolesList) {
                if (rolMap.get("name") != null) {
                    misRoles.add((String) rolMap.get("name"));
                }
            }
        } else {
            misRoles.add("ROLE_PACIENTE");
        }

        // 6. Generación del JWT usando la nueva clase JwtUtils
        // Delegamos la responsabilidad a security/JwtUtils
        return jwtUtils.createToken(username, misRoles);
    }
    
}
