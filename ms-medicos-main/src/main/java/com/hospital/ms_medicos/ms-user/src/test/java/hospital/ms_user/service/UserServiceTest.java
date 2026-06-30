package hospital.ms_user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import hospital.ms_user.model.User;
import hospital.ms_user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService; // Tu clase de servicio real

    @Test
    void cuandoBuscoPorEmail_yExiste_debeRetornarUsuario() {
        // GIVEN (Preparación)
        String email = "a.ovando@hospital.com";
        User usuarioFicticio = new User();
        usuarioFicticio.setAutoEmail(email);
        usuarioFicticio.setNombre("Anderson");
        
        when(userRepository.findByAutoEmail(email)).thenReturn(Optional.of(usuarioFicticio));

        // WHEN (Acción)
        Optional<User> resultado = userService.findByEmail(email);

        // THEN (Verificación)
        assertTrue(resultado.isPresent());
        assertEquals("Anderson", resultado.get().getNombre());
        verify(userRepository, times(1)).findByAutoEmail(email);
    }

    @Test
    void cuandoBuscoPorEmail_yNoExiste_debeRetornarOptionalVacio() {
    // GIVEN
    String email = "no.existe@hospital.com";
    when(userRepository.findByAutoEmail(email)).thenReturn(Optional.empty());

    // WHEN
    Optional<User> resultado = userService.findByEmail(email);

    // THEN
    assertFalse(resultado.isPresent());
    verify(userRepository, times(1)).findByAutoEmail(email);
    }

    @Test
    void cuandoListarUsuarios_debeRetornarListaDeUsuarios() {
        // GIVEN
        User u1 = new User();
        User u2 = new User();
        java.util.List<User> listaFicticia = java.util.Arrays.asList(u1, u2);
        
        when(userRepository.findAll()).thenReturn(listaFicticia);

        // WHEN (Cambia 'findAllUsers' por tu método real si se llama distinto)
        java.util.List<User> resultado = userService.findAll();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void cuandoGuardoUsuario_debeRetornarUsuarioGuardado() {
        // GIVEN
        User nuevoUsuario = new User();
        nuevoUsuario.setNombre("Anderson");
        nuevoUsuario.setApellido("Ovando");
        nuevoUsuario.setTelefono("912345678");
        nuevoUsuario.setContrasena("clave123");
        
        // Creamos el objeto Rol real que espera tu modelo
        hospital.ms_user.model.Rol rolFicticio = new hospital.ms_user.model.Rol();
        // Si tu clase Rol tiene métodos como setId() o setNombre(), puedes usarlos aquí, por ejemplo:
        // rolFicticio.setNombre("ROLE_USER");
        
        // Creamos un Set e insertamos el rol
        java.util.Set<hospital.ms_user.model.Rol> rolesSet = new java.util.HashSet<>();
        rolesSet.add(rolFicticio);
        
        // Asignamos el Set correcto al usuario
        nuevoUsuario.setRoles(rolesSet);

        // Simulamos las respuestas de las validaciones en la Base de Datos
        when(userRepository.existsByAutoEmail(anyString())).thenReturn(false);
        when(userRepository.existsByTelefono("912345678")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(nuevoUsuario);

        // WHEN
        User resultado = userService.save(nuevoUsuario); 

        // THEN
        assertNotNull(resultado);
        assertEquals("anderson", resultado.getNombre().toLowerCase());
        assertTrue(resultado.isActive());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void cuandoEliminoPorEmail_yExiste_debeEliminarExitosamente() {
        // GIVEN
        String email = "and.ovando@hospital.com";
        // Simulamos que el correo SÍ existe en la BD
        when(userRepository.existsByAutoEmail(email)).thenReturn(true);
        doNothing().when(userRepository).deleteByAutoEmail(email);

        // WHEN
        userService.deleteByEmail(email);

        // THEN
        verify(userRepository, times(1)).existsByAutoEmail(email);
        verify(userRepository, times(1)).deleteByAutoEmail(email);
    }


    @Test
    void cuandoEliminoPorEmail_yNoExiste_debeLanzarBadRequestException() {
        // GIVEN
        String email = "no.existe@hospital.com";
        // Simulamos que el correo NO existe en la BD
        when(userRepository.existsByAutoEmail(email)).thenReturn(false);

        // WHEN & THEN
        // Verificamos que se lance la excepción correcta esperada por Duoc
        hospital.ms_user.exception.BadRequestException excepcion = assertThrows(
            hospital.ms_user.exception.BadRequestException.class,
            () -> userService.deleteByEmail(email)
        );

        assertEquals("El usuario con el correo 'no.existe@hospital.com' no existe en el sistema.", excepcion.getMessage());
        verify(userRepository, times(1)).existsByAutoEmail(email);
        verify(userRepository, never()).deleteByAutoEmail(email); // Jamás debe intentar borrar
    }

    @Test
    void cuandoFindAll_debeRetornarListaDeUsuarios() {
        // GIVEN
        User u1 = new User();
        User u2 = new User();
        when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(u1, u2));

        // WHEN
        java.util.List<User> resultado = userService.findAll();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void cuandoFindById_yExiste_debeRetornarUsuario() {
        // GIVEN
        Long id = 1L;
        User usuario = new User();
        usuario.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(usuario));

        // WHEN
        Optional<User> resultado = userService.findById(id);

        // THEN
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(userRepository, times(1)).findById(id);
    }

}