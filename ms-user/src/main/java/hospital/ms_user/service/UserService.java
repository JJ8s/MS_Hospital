package hospital.ms_user.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hospital.ms_user.exception.BadRequestException;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.UserRepository;
import hospital.ms_user.repository.RolRepository; // <-- Importamos el nuevo repositorio

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository; // <-- Inyectamos RolRepository

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByAutoEmail(email);
    }

    public User save(User user) {
        // 1. Generación automática del correo
        String nombreLimpio = user.getNombre().toLowerCase().trim().replace(" ", "");
        String apellidoLimpio = user.getApellido().toLowerCase().trim().replace(" ", "");
        String correoGenerado = nombreLimpio + "." + apellidoLimpio + "@hospital.com";
        user.setAutoEmail(correoGenerado);

        // 2. Validaciones de integridad
        if (userRepository.existsByAutoEmail(user.getAutoEmail())) {
            throw new BadRequestException("El correo institucional '" + user.getAutoEmail() + "' ya está registrado");
        }
        
        if (userRepository.existsByTelefono(user.getTelefono())) {
            throw new BadRequestException("El número de teléfono ya está en uso");
        }

        // 3. ASIGNACIÓN ASOCIADA DESDE REPOSITORIO (Soluciona el Error 500)
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Rol> nuevosRoles = new HashSet<>();
            
            // Buscamos el rol de la base de datos por ID para evitar que Hibernate intente duplicarlo
            Rol defaultRol = rolRepository.findById(1L)
                .orElseThrow(() -> new BadRequestException("Error crítico: El Rol con ID 1 (ROLE_ADMIN) no existe en la base de datos."));
            
            nuevosRoles.add(defaultRol);
            user.setRoles(nuevosRoles);
        }

        // 4. Encriptación de la contraseña
        String contraseñaEncriptada = passwordEncoder.encode(user.getContrasena());
        user.setContrasena(contraseñaEncriptada);
        
        // 5. Activación automática de la cuenta
        user.setActive(true);
        
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}