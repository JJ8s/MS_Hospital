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
    private UserService userService; 

    @Test
    void cuandoBuscoPorEmail_yExiste_debeRetornarUsuario() {
        
        String email = "a.ovando@hospital.com";
        User usuarioFicticio = new User();
        usuarioFicticio.setAutoEmail(email);
        usuarioFicticio.setNombre("Anderson");
        
        when(userRepository.findByAutoEmail(email)).thenReturn(Optional.of(usuarioFicticio));

        
        Optional<User> resultado = userService.findByEmail(email);

        
        assertTrue(resultado.isPresent());
        assertEquals("Anderson", resultado.get().getNombre());
        verify(userRepository, times(1)).findByAutoEmail(email);
    }

    @Test
    void cuandoBuscoPorEmail_yNoExiste_debeRetornarOptionalVacio() {
    
    String email = "no.existe@hospital.com";
    when(userRepository.findByAutoEmail(email)).thenReturn(Optional.empty());

    
    Optional<User> resultado = userService.findByEmail(email);

    
    assertFalse(resultado.isPresent());
    verify(userRepository, times(1)).findByAutoEmail(email);
    }

    @Test
    void cuandoListarUsuarios_debeRetornarListaDeUsuarios() {
        
        User u1 = new User();
        User u2 = new User();
        java.util.List<User> listaFicticia = java.util.Arrays.asList(u1, u2);
        
        when(userRepository.findAll()).thenReturn(listaFicticia);

        
        java.util.List<User> resultado = userService.findAll();

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void cuandoGuardoUsuario_debeRetornarUsuarioGuardado() {
        
        User nuevoUsuario = new User();
        nuevoUsuario.setNombre("Anderson");
        nuevoUsuario.setApellido("Ovando");
        nuevoUsuario.setTelefono("912345678");
        nuevoUsuario.setContrasena("clave123");
        
        
        hospital.ms_user.model.Rol rolFicticio = new hospital.ms_user.model.Rol();
        
        
        
        
        java.util.Set<hospital.ms_user.model.Rol> rolesSet = new java.util.HashSet<>();
        rolesSet.add(rolFicticio);
        
        
        nuevoUsuario.setRoles(rolesSet);

        
        when(userRepository.existsByAutoEmail(anyString())).thenReturn(false);
        when(userRepository.existsByTelefono("912345678")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(nuevoUsuario);

        
        User resultado = userService.save(nuevoUsuario); 

        
        assertNotNull(resultado);
        assertEquals("anderson", resultado.getNombre().toLowerCase());
        assertTrue(resultado.isActive());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void cuandoEliminoPorEmail_yExiste_debeEliminarExitosamente() {
        
        String email = "and.ovando@hospital.com";
        
        when(userRepository.existsByAutoEmail(email)).thenReturn(true);
        doNothing().when(userRepository).deleteByAutoEmail(email);

        
        userService.deleteByEmail(email);

        
        verify(userRepository, times(1)).existsByAutoEmail(email);
        verify(userRepository, times(1)).deleteByAutoEmail(email);
    }


    @Test
    void cuandoEliminoPorEmail_yNoExiste_debeLanzarBadRequestException() {
        
        String email = "no.existe@hospital.com";
        
        when(userRepository.existsByAutoEmail(email)).thenReturn(false);

        
        
        hospital.ms_user.exception.BadRequestException excepcion = assertThrows(
            hospital.ms_user.exception.BadRequestException.class,
            () -> userService.deleteByEmail(email)
        );

        assertEquals("El usuario con el correo 'no.existe@hospital.com' no existe en el sistema.", excepcion.getMessage());
        verify(userRepository, times(1)).existsByAutoEmail(email);
        verify(userRepository, never()).deleteByAutoEmail(email); 
    }

    @Test
    void cuandoFindAll_debeRetornarListaDeUsuarios() {
        
        User u1 = new User();
        User u2 = new User();
        when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(u1, u2));

        
        java.util.List<User> resultado = userService.findAll();

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void cuandoFindById_yExiste_debeRetornarUsuario() {
        
        Long id = 1L;
        User usuario = new User();
        usuario.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(usuario));

        
        Optional<User> resultado = userService.findById(id);

        
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(userRepository, times(1)).findById(id);
    }

}