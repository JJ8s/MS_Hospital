 package hospital.ms_auth.service;

 import hospital.ms_auth.exception.UnauthorizedException;
 import hospital.ms_auth.model.AuthCliente;
 import hospital.ms_auth.security.JwtUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AuthCliente authCliente;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired 
    private PasswordEncoder passwordEncoder;

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

        // 🔄 Buscamos tanto en 'role' (singular) como en 'roles' (plural) para blindar el microservicio
        Object rolesObj = user.get("role") != null ? user.get("role") : user.get("roles");

        if (rolesObj != null) {
            if (rolesObj instanceof List) {
                List<Map<String, Object>> rolesList = (List<Map<String, Object>>) rolesObj;
                for (Map<String, Object> r : rolesList) {
                    String roleName = (String) r.get("name");
                    if (roleName != null) misRoles.add(roleName);
                }
            } else if (rolesObj instanceof Map) {
                // Si viene como un solo objeto de rol y no como lista
                Map<String, Object> r = (Map<String, Object>) rolesObj;
                String roleName = (String) r.get("name");
                if (roleName != null) misRoles.add(roleName);
            } else if (rolesObj instanceof String) {
                // Si viene como un texto plano puro "ADMIN"
                String roleName = (String) rolesObj;
                misRoles.add(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName);
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

