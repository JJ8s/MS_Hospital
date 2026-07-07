package hospital.ms_user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hospital.ms_user.exception.BadRequestException;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
        String nombreLimpio = user.getNombre().toLowerCase().trim().replace(" ", "");
        String apellidoLimpio = user.getApellido().toLowerCase().trim().replace(" ", "");
        
        String correoGenerado = "";
        int letrasNombre = 3; 
        boolean correoDuplicado = true;

        
        while (correoDuplicado) {
            int limite = Math.min(letrasNombre, nombreLimpio.length());
            String subNombre = nombreLimpio.substring(0, limite);
            
            
            if (letrasNombre > nombreLimpio.length()) {
                int sufijoNumerico = letrasNombre - nombreLimpio.length();
                correoGenerado = subNombre + sufijoNumerico + "." + apellidoLimpio + "@hospital.com";
            } else {
                correoGenerado = subNombre + "." + apellidoLimpio + "@hospital.com";
            }

            
            if (userRepository.existsByAutoEmail(correoGenerado)) {
                letrasNombre++; 
            } else {
                correoDuplicado = false; 
            }
        }
        
        
        user.setAutoEmail(correoGenerado);

        
        if (userRepository.existsByTelefono(user.getTelefono())) {
            throw new BadRequestException("El número de teléfono ya está en uso");
        }

        
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new BadRequestException("El usuario debe tener al menos un rol asignado obligatoriamente.");
        }

        
        String contraseñaEncriptada = passwordEncoder.encode(user.getContrasena());
        user.setContrasena(contraseñaEncriptada);
        
        
        user.setActive(true);
        
        return userRepository.save(user);
    }

    

    @Transactional
    public void deleteByEmail(String email) {
        if (!userRepository.existsByAutoEmail(email)) {
            throw new BadRequestException("El usuario con el correo '" + email + "' no existe en el sistema.");
        }
        userRepository.deleteByAutoEmail(email);
    }
}