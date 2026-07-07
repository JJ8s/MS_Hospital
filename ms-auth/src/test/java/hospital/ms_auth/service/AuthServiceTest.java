package hospital.ms_auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hospital.ms_auth.exception.UnauthorizedException;
import hospital.ms_auth.model.AuthCliente;
import hospital.ms_auth.security.JwtUtils;

class AuthServiceTest {

    // Declaramos los componentes como atributos limpios de la clase
    private AuthCliente authCliente;
    private JwtUtils jwtUtils;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthService authService;

    private Map<String, Object> mockUserResponse;

    @BeforeEach
    void setUp() {
        // 🚀 Solución raíz: Creamos los mocks mediante métodos directos independientes del IDE
        authCliente = mock(AuthCliente.class);
        jwtUtils = mock(JwtUtils.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        
        // Inicializamos el servicio que vamos a probar
        authService = new AuthService();
        
        // Inyectamos los mocks programáticamente dentro de los campos @Autowired del servicio
        ReflectionTestUtils.setField(authService, "authCliente", authCliente);
        ReflectionTestUtils.setField(authService, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);

        // Estructura simulada para las respuestas de ms-user
        mockUserResponse = new HashMap<>();
        mockUserResponse.put("autoEmail", "admin.prueba@hospital.com");
        mockUserResponse.put("contrasena", "passwordBcryptSimulado123");

        List<Map<String, Object>> rolesList = new ArrayList<>();
        Map<String, Object> roleAdmin = new HashMap<>();
        roleAdmin.put("name", "ROLE_ADMIN");
        rolesList.add(roleAdmin);
        mockUserResponse.put("roles", rolesList);
    }

    @Test
    void login_DeberiaRetornarToken_CuandoCredencialesYRolesSonCorrectos() {
        String email = "admin.prueba@hospital.com";
        String clavePlana = "HospitalSecure2026";
        String tokenEsperado = "eyJhbGciOiJIUzI1Ni...tokenDePrueba";

        when(authCliente.getUserByAutoEmail(email)).thenReturn(mockUserResponse);
        when(passwordEncoder.matches(clavePlana, "passwordBcryptSimulado123")).thenReturn(true);
        when(jwtUtils.createToken(eq(email), anyList())).thenReturn(tokenEsperado);

        String tokenObtenido = authService.login(email, clavePlana);

        assertNotNull(tokenObtenido);
        assertEquals(tokenEsperado, tokenObtenido);
        
        verify(authCliente, times(1)).getUserByAutoEmail(email);
        verify(passwordEncoder, times(1)).matches(clavePlana, "passwordBcryptSimulado123");
    }

    @Test
    void login_DeberiaLanzarUnauthorizedException_CuandoCorreoNoExiste() {
        String emailNoRegistrado = "inexistente@hospital.com";
        when(authCliente.getUserByAutoEmail(emailNoRegistrado)).thenReturn(null);

        UnauthorizedException excepcion = assertThrows(UnauthorizedException.class, () -> {
            authService.login(emailNoRegistrado, "CualquierClave123");
        });

        assertEquals("El correo 'inexistente@hospital.com' no está registrado.", excepcion.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_DeberiaLanzarUnauthorizedException_CuandoContrasenaEsIncorrecta() {
        String email = "admin.prueba@hospital.com";
        String claveInvalida = "ClaveIncorrecta123";

        when(authCliente.getUserByAutoEmail(email)).thenReturn(mockUserResponse);
        when(passwordEncoder.matches(claveInvalida, "passwordBcryptSimulado123")).thenReturn(false);

        UnauthorizedException excepcion = assertThrows(UnauthorizedException.class, () -> {
            authService.login(email, claveInvalida);
        });

        assertEquals("La contraseña ingresada es incorrecta.", excepcion.getMessage());
        verify(jwtUtils, never()).createToken(anyString(), anyList());
    }
}