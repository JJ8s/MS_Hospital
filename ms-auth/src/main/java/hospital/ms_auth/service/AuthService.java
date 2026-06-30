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

    @SuppressWarnings("unchecked")
    public String login(String email, String contrasena) {
        // Llama a ms-user a través de la interfaz Feign que acabamos de crear
        Map<String, Object> user = authCliente.getUserByAutoEmail(email);
        
        if (user == null) {
            throw new UnauthorizedException("El correo '" + email + "' no está registrado.");
        }

        String storedPassword = (String) user.get("contrasena");

        if (!passwordEncoder.matches(contrasena, storedPassword)) {
            throw new UnauthorizedException("La contraseña ingresada es incorrecta.");
        }

        List<String> misRoles = new ArrayList<>();
        
        // EXTRAER ROLES REALES: Mapeamos la colección 'roles' que viene desde ms-user
        if (user.get("roles") != null) {
            List<Map<String, Object>> rolesList = (List<Map<String, Object>>) user.get("roles");
            for (Map<String, Object> r : rolesList) {
                String roleName = (String) r.get("name"); // Extrae 'ROLE_ADMIN', 'ROLE_MEDICO', etc.
                if (roleName != null) {
                    misRoles.add(roleName);
                }
            }
        }

        // Si por alguna razón la lista quedó vacía, dejamos un fallback seguro
        if (misRoles.isEmpty()) {
            misRoles.add("ROLE_PACIENTE");
        }
        
        // Genera el token con el correo y los roles reales obtenidos de la base de datos
        return jwtUtils.createToken(email, misRoles);
    }
}