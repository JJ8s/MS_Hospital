package hospital.ms_user.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Importación necesaria para eliminar

import hospital.ms_user.exception.BadRequestException;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.UserRepository;
import hospital.ms_user.repository.RolRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

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
        // 1. Limpieza y normalización de texto (quita espacios y pasa a minúsculas)
        String nombreLimpio = user.getNombre().toLowerCase().trim().replace(" ", "");
        String apellidoLimpio = user.getApellido().toLowerCase().trim().replace(" ", "");
        
        String correoGenerado = "";
        int letrasNombre = 3; // Empezamos evaluando con un mínimo de 3 letras
        boolean correoDuplicado = true;

        // 2. Bucle de resolución de colisiones en cascada (3 letras -> 4 letras -> ... -> nombre + número)
        while (correoDuplicado) {
            int limite = Math.min(letrasNombre, nombreLimpio.length());
            String subNombre = nombreLimpio.substring(0, limite);
            
            // Si el contador supera el tamaño del nombre, añadimos un sufijo numérico incremental
            if (letrasNombre > nombreLimpio.length()) {
                int sufijoNumerico = letrasNombre - nombreLimpio.length();
                correoGenerado = subNombre + sufijoNumerico + "." + apellidoLimpio + "@hospital.com";
            } else {
                correoGenerado = subNombre + "." + apellidoLimpio + "@hospital.com";
            }

            // Comprobamos la disponibilidad real en la base de datos
            if (userRepository.existsByAutoEmail(correoGenerado)) {
                letrasNombre++; // Siguiente iteración intentará con una letra extra
            } else {
                correoDuplicado = false; // Rompemos el ciclo al encontrar un correo libre
            }
        }
        
        // Asignamos el email institucional único calculado
        user.setAutoEmail(correoGenerado);

        // 3. Validación de integridad del Teléfono
        if (userRepository.existsByTelefono(user.getTelefono())) {
            throw new BadRequestException("El número de teléfono ya está en uso");
        }

        // 4. ASIGNACIÓN ASOCIADA DESDE REPOSITORIO
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Rol> nuevosRoles = new HashSet<>();
            
            Rol defaultRol = rolRepository.findById(1L)
                .orElseThrow(() -> new BadRequestException("Error crítico: El Rol con ID 1 (ROLE_ADMIN) no existe en la base de datos."));
            
            nuevosRoles.add(defaultRol);
            user.setRoles(nuevosRoles);
        }

        // 5. Encriptación de la contraseña
        String contraseñaEncriptada = passwordEncoder.encode(user.getContrasena());
        user.setContrasena(contraseñaEncriptada);
        
        // 6. Activación automática de la cuenta
        user.setActive(true);
        
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Nuevo método: Elimina un usuario a través de su correo electrónico institucional.
     * La anotación @Transactional asegura que se cree el contexto de transacción adecuado 
     * para operaciones de borrado personalizadas.
     */
    @Transactional
    public void deleteByEmail(String email) {
        if (!userRepository.existsByAutoEmail(email)) {
            throw new BadRequestException("El usuario con el correo '" + email + "' no existe en el sistema.");
        }
        userRepository.deleteByAutoEmail(email);
    }
}