package hospital.ms_user.service;

import hospital.ms_user.dto.UserDto;
import hospital.ms_user.dto.UserResponseDto;
import hospital.ms_user.mapper.UserMapper;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.RolRepository;
import hospital.ms_user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Tests unitarios")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User userEntity;
    private Rol rolPaciente;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setNombre("Juan");
        userDto.setApellido("Perez");
        userDto.setTelefono("987654321");
        userDto.setContrasena("password123");
        userDto.setRoles(List.of("PACIENTE"));

        userEntity = new User();
        userEntity.setNombre("Juan");
        userEntity.setApellido("Perez");
        userEntity.setTelefono("987654321");
        userEntity.setContrasena("password123");

        rolPaciente = new Rol(1L, "PACIENTE");
    }

    // ---------- registrarUsuario ----------

    @Test
    @DisplayName("Debe registrar un usuario correctamente y generar su correo único")
    void registrarUsuario_exitoso() {
        when(userRepository.existsByTelefono("987654321")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(rolRepository.findByName("PACIENTE")).thenReturn(Optional.of(rolPaciente));
        when(passwordEncoder.encode("password123")).thenReturn("HASHED");
        when(userRepository.existsByAutoEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setAutoEmail("jua.per@hospital.com");
        when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

        UserResponseDto resultado = userService.registrarUsuario(userDto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getAutoEmail()).isEqualTo("jua.per@hospital.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Debe generar un correo con sufijo numérico si ya existe uno igual (homónimos)")
    void registrarUsuario_correoDuplicado_generaSufijo() {
        when(userRepository.existsByTelefono(anyString())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(rolRepository.findByName("PACIENTE")).thenReturn(Optional.of(rolPaciente));
        when(passwordEncoder.encode(anyString())).thenReturn("HASHED");
        // La primera combinación de correo ya existe, forzando el algoritmo a probar la siguiente
        when(userRepository.existsByAutoEmail(anyString()))
                .thenReturn(true, true, true, false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(new UserResponseDto());

        userService.registrarUsuario(userDto);

        verify(userRepository, atLeast(4)).existsByAutoEmail(anyString());
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si el teléfono ya está registrado")
    void registrarUsuario_telefonoDuplicado_lanzaExcepcion() {
        when(userRepository.existsByTelefono("987654321")).thenReturn(true);

        assertThatThrownBy(() -> userService.registrarUsuario(userDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("teléfono ya se encuentra registrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si el rol indicado no existe")
    void registrarUsuario_rolInexistente_lanzaExcepcion() {
        when(userRepository.existsByTelefono(anyString())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(rolRepository.findByName("PACIENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.registrarUsuario(userDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");

        verify(userRepository, never()).save(any());
    }

    // ---------- obtenerTodos ----------

    @Test
    @DisplayName("Debe retornar la lista de usuarios mapeada a DTO")
    void obtenerTodos_retornaListaMapeada() {
        User otroUsuario = new User();
        when(userRepository.findAll()).thenReturn(List.of(userEntity, otroUsuario));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(new UserResponseDto());

        List<UserResponseDto> resultado = userService.obtenerTodos();

        assertThat(resultado).hasSize(2);
        verify(userMapper, times(2)).toResponseDto(any(User.class));
    }

    // ---------- deleteByEmail ----------

    @Test
    @DisplayName("Debe desactivar (borrado lógico) un usuario existente")
    void deleteByEmail_usuarioExistente_desactiva() {
        userEntity.setActive(true);
        when(userRepository.findByAutoEmail("jua.per@hospital.com")).thenReturn(Optional.of(userEntity));

        userService.deleteByEmail("jua.per@hospital.com");

        assertThat(userEntity.isActive()).isFalse();
        verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si el correo no existe al desactivar")
    void deleteByEmail_usuarioNoExiste_lanzaExcepcion() {
        when(userRepository.findByAutoEmail("noexiste@hospital.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteByEmail("noexiste@hospital.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontró ningún usuario");

        verify(userRepository, never()).save(any());
    }

    // ---------- findByEmail ----------

    @Test
    @DisplayName("Debe retornar el usuario cuando el correo existe")
    void findByEmail_existente_retornaOptionalConUsuario() {
        when(userRepository.findByAutoEmail("jua.per@hospital.com")).thenReturn(Optional.of(userEntity));

        Optional<User> resultado = userService.findByEmail("jua.per@hospital.com");

        assertThat(resultado).isPresent().contains(userEntity);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el correo no existe")
    void findByEmail_inexistente_retornaOptionalVacio() {
        when(userRepository.findByAutoEmail("noexiste@hospital.com")).thenReturn(Optional.empty());

        Optional<User> resultado = userService.findByEmail("noexiste@hospital.com");

        assertThat(resultado).isEmpty();
    }
}