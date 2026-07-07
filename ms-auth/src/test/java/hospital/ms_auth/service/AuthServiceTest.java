package hospital.ms_auth.service;

import hospital.ms_auth.exception.UnauthorizedException;
import hospital.ms_auth.model.AuthCliente;
import hospital.ms_auth.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthCliente authCliente;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_credencialesValidas_retornaTokenConRolesDelUsuario() {
        String email = "admin@hospital.com";
        Map<String, Object> user = Map.of(
                "contrasena", "$2a$10$hash",
                "roles", List.of(Map.of("name", "ROLE_ADMIN"), Map.of("name", "ROLE_MEDICO"))
        );

        when(authCliente.getUserByAutoEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("123456", "$2a$10$hash")).thenReturn(true);
        when(jwtUtils.createToken(email, List.of("ROLE_ADMIN", "ROLE_MEDICO"))).thenReturn("jwt-token");

        String token = authService.login(email, "123456");

        assertThat(token).isEqualTo("jwt-token");
        verify(jwtUtils).createToken(email, List.of("ROLE_ADMIN", "ROLE_MEDICO"));
    }

    @Test
    void login_usuarioSinRoles_usaRolPacientePorDefecto() {
        String email = "paciente@hospital.com";
        Map<String, Object> user = Map.of("contrasena", "$2a$10$hash");

        when(authCliente.getUserByAutoEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("123456", "$2a$10$hash")).thenReturn(true);
        when(jwtUtils.createToken(email, List.of("ROLE_PACIENTE"))).thenReturn("jwt-paciente");

        String token = authService.login(email, "123456");

        assertThat(token).isEqualTo("jwt-paciente");
        verify(jwtUtils).createToken(email, List.of("ROLE_PACIENTE"));
    }

    @Test
    void login_correoNoRegistrado_lanzaUnauthorizedException() {
        when(authCliente.getUserByAutoEmail("nadie@hospital.com")).thenReturn(null);

        assertThatThrownBy(() -> authService.login("nadie@hospital.com", "123456"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("registrado");

        verify(passwordEncoder, never()).matches("123456", null);
        verify(jwtUtils, never()).createToken(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void login_passwordIncorrecta_lanzaUnauthorizedException() {
        String email = "admin@hospital.com";
        Map<String, Object> user = Map.of("contrasena", "$2a$10$hash");

        when(authCliente.getUserByAutoEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("incorrecta", "$2a$10$hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(email, "incorrecta"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("incorrecta");

        verify(jwtUtils, never()).createToken(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyList());
    }
}
