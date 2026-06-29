package hospital.ms_user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
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
