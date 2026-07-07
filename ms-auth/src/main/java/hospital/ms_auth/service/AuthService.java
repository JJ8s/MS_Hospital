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
        
        Map<String, Object> user = authCliente.getUserByAutoEmail(email);
        
        if (user == null) {
            throw new UnauthorizedException("El correo '" + email + "' no está registrado.");
        }

        String storedPassword = (String) user.get("contrasena");

        if (!passwordEncoder.matches(contrasena, storedPassword)) {
            throw new UnauthorizedException("La contraseña ingresada es incorrecta.");
        }

        List<String> misRoles = new ArrayList<>();
        
        
        if (user.get("roles") != null) {
            List<Map<String, Object>> rolesList = (List<Map<String, Object>>) user.get("roles");
            for (Map<String, Object> r : rolesList) {
                String roleName = (String) r.get("name"); 
                if (roleName != null) {
                    misRoles.add(roleName);
                }
            }
        }

        
        if (misRoles.isEmpty()) {
            misRoles.add("ROLE_PACIENTE");
        }
        
        
        return jwtUtils.createToken(email, misRoles);
    }
}