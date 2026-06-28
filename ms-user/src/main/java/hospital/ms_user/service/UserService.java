package hospital.ms_user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.mapper.UserMapper;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User save(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        
        // CONFIGURACIÓN: Guardamos la contraseña en TEXTO PLANO
        // No llamamos a passwordEncoder.encode()
        user.setContrasena(dto.getContrasena()); 

        String correoGenerado = dto.generateSystemEmail();
        user.setAutoEmail(correoGenerado); 

        if (userRepository.existsByAutoEmail(correoGenerado)) {
            throw new RuntimeException("El correo generado ya existe en db_user");
        }

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByAutoEmail(String email) {
        return userRepository.findByAutoEmail(email);
    }
}