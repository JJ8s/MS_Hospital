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

    // Puedes mantener la inyección si la usas en otros lados, 
    // pero no la usaremos en el método login para texto plano.
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String login(String email, String contrasena) {
        Map<String, Object> user = authCliente.getUserByAutoEmail(email);
        
        if (user == null) {
            throw new UnauthorizedException("El correo '" + email + "' no está registrado.");
        }

        // Extraemos la contraseña de la base de datos (texto plano ahora)
        String storedPassword = (String) user.get("contrasena");

        // CONFIGURACIÓN: Comparación directa de Strings
        if (!contrasena.equals(storedPassword)) {
            throw new UnauthorizedException("La contraseña ingresada es incorrecta.");
        }

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
            misRoles.add("ROLE_PACIENTE");
        }

        return jwtUtils.createToken(email, misRoles);
    }
}