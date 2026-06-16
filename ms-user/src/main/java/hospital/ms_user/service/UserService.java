package hospital.ms_user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import hospital.ms_user.exception.BadRequestException;
import hospital.ms_user.model.Usuario;
import hospital.ms_user.repository.UsuarioRepository;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Usuario> findAll() {
        return userRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<Usuario> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Usuario save(Usuario user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("El nombre de usuario '" + user.getUsername() + "' ya está en uso");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("El correo electrónico '" + user.getEmail() + "' ya está registrado");
        }
        

        String contraseñaEncriptada = passwordEncoder.encode(user.getPassword());
        user.setPassword(contraseñaEncriptada);
        
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    
}
