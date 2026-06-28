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

    // AÑADE ESTO: Requerido por el UserController
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // AÑADE ESTO: Requerido por el UserController
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User save(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        String correoGenerado = dto.generateSystemEmail();
        
        // Correcto: Ahora solo asignas el correo al campo de identidad
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