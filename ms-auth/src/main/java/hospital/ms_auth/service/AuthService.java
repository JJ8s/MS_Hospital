package hospital.ms_auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String login(String email, String contrasena) {
        // 1. Buscamos al usuario mediante el nuevo identificador institucional (autoEmail)
        // Nota: Asegúrate de que AuthCliente tenga el método getUserByEmail
        Map<String, Object> user = authCliente.getUserByAutoEmail(email);
        
        // 2. Validamos si el usuario existe en db_user
        if (user == null) {
            throw new UnauthorizedException("El correo '" + email + "' no está registrado en el hospital.");
        }

        // 3. Extraemos la contraseña (ahora mapeada como 'contrasena' en ms-user)
        String encryptedPassword = (String) user.get("contrasena");

        // 4. Comparamos las credenciales con BCrypt
        if (!passwordEncoder.matches(contrasena, encryptedPassword)) {
            throw new UnauthorizedException("La contraseña ingresada para " + email + " es incorrecta.");
        }

        // 5. Extraer roles para incluirlos en los claims del JWT
        List<String> misRoles = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rolesList = (List<Map<String, Object>>) user.get("roles");
        
        if (rolesList != null && !rolesList.isEmpty()) {
            for (Map<String, Object> rolMap : rolesList) {
                if (rolMap.get("name") != null) {
                    misRoles.add((String) rolMap.get("name"));
                }
            }
        } else {
            // Rol por defecto si no tiene asignados en el Paso 2
            misRoles.add("ROLE_PACIENTE");
        }

        // 6. Generación del JWT usando el email como identidad principal
        return jwtUtils.createToken(email, misRoles);
    }
}