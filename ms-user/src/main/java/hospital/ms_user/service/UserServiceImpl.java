package hospital.ms_user.service;

import hospital.ms_user.dto.UserDto;
import hospital.ms_user.dto.UserResponseDto;
import hospital.ms_user.mapper.UserMapper;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.RolRepository;
import hospital.ms_user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // Inyección limpia por constructor (Práctica Profesional de Spring)
    public UserServiceImpl(UserRepository userRepository, RolRepository rolRepository,
                           UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDto registrarUsuario(UserDto dto) {
        // 1. Limpiar espacios y pasar a minúsculas para estandarizar
        String nombreLimpio = dto.getNombre().toLowerCase().replace(" ", "");
        String apellidoLimpio = dto.getApellido().toLowerCase().replace(" ", "");

        // 2. Llamar al método dinámico que calcula el correo único incremental
        String emailGenerado = generarEmailUnico(nombreLimpio, apellidoLimpio);

        // 3. Validar únicamente que el teléfono no esté repetido en XAMPP
        if (userRepository.existsByTelefono(dto.getTelefono())) {
            throw new RuntimeException("El número de teléfono ya se encuentra registrado.");
        }

        // 4. Convertir DTO a Entidad mediante nuestro Mapper
        User usuario = userMapper.toEntity(dto);
        usuario.setAutoEmail(emailGenerado);
        usuario.setActive(true);

        // 5. Encriptar la contraseña asignada por el usuario de manera segura con BCrypt
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        // 6. Buscar y asignar los roles (ADMIN, MEDICO, PACIENTE)
        Set<Rol> rolesAsignados = new HashSet<>();
        for (String nombreRol : dto.getRoles()) {
            Rol rol = rolRepository.findByName(nombreRol.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("El rol '" + nombreRol + "' no existe."));
            rolesAsignados.add(rol);
        }
        usuario.setRoles(rolesAsignados);

        // 7. Guardar en XAMPP (MySQL) y retornar la respuesta limpia sin contraseña
        User usuarioGuardado = userRepository.save(usuario);
        return userMapper.toResponseDto(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> obtenerTodos() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        User usuario = userRepository.findByAutoEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún usuario con el correo: " + email));
        usuario.setActive(false); // Realizamos el borrado lógico seguro
        userRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByAutoEmail(email);
    }

    /**
     * Método auxiliar privado: Se deja al final de la clase para mantener limpio
     * el orden visual de los métodos del contrato de la interfaz.
     */
    private String generarEmailUnico(String nombre, String apellido) {
        int longitudMinima = 3;
        int maxLongitudNombre = nombre.length();
        int maxLongitudApellido = apellido.length();

        int intentoLongitud = longitudMinima;
        int contadorHomonimos = 1;
        String emailPropuesto;

        while (true) {
            int finNombre = Math.min(intentoLongitud, maxLongitudNombre);
            int finApellido = Math.min(intentoLongitud, maxLongitudApellido);

            String subNombre = nombre.substring(0, finNombre);
            String subApellido = apellido.substring(0, finApellido);

            if (intentoLongitud > maxLongitudNombre && intentoLongitud > maxLongitudApellido) {
                emailPropuesto = subNombre + "." + subApellido + contadorHomonimos + "@hospital.com";
                contadorHomonimos++;
            } else {
                emailPropuesto = subNombre + "." + subApellido + "@hospital.com";
            }

            if (!userRepository.existsByAutoEmail(emailPropuesto)) {
                return emailPropuesto;
            }

            intentoLongitud++;
        }
    }
}