package hospital.ms_user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hospital.ms_user.dto.UserDto;
import hospital.ms_user.dto.UserResponseDto;
import hospital.ms_user.model.User;
import hospital.ms_user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// addFilters = false: desactivamos la cadena de Spring Security para aislar
// y probar únicamente la capa web (controller + GlobalExceptionHandler).
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController - Tests de capa web (MockMvc)")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDto userDtoValido() {
        UserDto dto = new UserDto();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setTelefono("987654321");
        dto.setContrasena("password123");
        dto.setRoles(List.of("PACIENTE"));
        return dto;
    }

    // ---------- POST /api/users ----------

    @Test
    @DisplayName("POST /api/users - crea usuario válido y retorna 201")
    void createUser_datosValidos_retorna201() throws Exception {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setAutoEmail("jua.per@hospital.com");
        responseDto.setNombre("Juan");

        when(userService.registrarUsuario(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autoEmail").value("jua.per@hospital.com"))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(userService, times(1)).registrarUsuario(any());
    }

    @Test
    @DisplayName("POST /api/users - datos inválidos retorna 400 con detalle de validaciones")
    void createUser_datosInvalidos_retorna400() throws Exception {
        UserDto dtoInvalido = new UserDto();
        dtoInvalido.setNombre(""); // vacío -> viola @NotBlank
        dtoInvalido.setTelefono("123"); // muy corto -> viola @Size(min = 9)
        dtoInvalido.setContrasena("123"); // muy corta -> viola @Size(min = 8)
        dtoInvalido.setRoles(List.of()); // vacío -> viola @NotEmpty

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validations.nombre").exists());

        verify(userService, never()).registrarUsuario(any());
    }

    // ---------- GET /api/users ----------

    @Test
    @DisplayName("GET /api/users - retorna la lista de usuarios")
    void listarUsuarios_retorna200ConLista() throws Exception {
        UserResponseDto u1 = new UserResponseDto();
        u1.setNombre("Juan");
        UserResponseDto u2 = new UserResponseDto();
        u2.setNombre("Maria");

        when(userService.obtenerTodos()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    // ---------- DELETE /api/users/email/{email} ----------

    @Test
    @DisplayName("DELETE /api/users/email/{email} - desactiva usuario correctamente")
    void eliminarPorEmail_existente_retorna200() throws Exception {
        doNothing().when(userService).deleteByEmail("jua.per@hospital.com");

        mockMvc.perform(delete("/api/users/email/jua.per@hospital.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("desactivado")));

        verify(userService).deleteByEmail("jua.per@hospital.com");
    }

    @Test
    @DisplayName("DELETE /api/users/email/{email} - usuario inexistente propaga error de negocio (400)")
    void eliminarPorEmail_inexistente_retorna400() throws Exception {
        doThrow(new RuntimeException("No se encontró ningún usuario con el correo: noexiste@hospital.com"))
                .when(userService).deleteByEmail("noexiste@hospital.com");

        mockMvc.perform(delete("/api/users/email/noexiste@hospital.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    // ---------- DELETE /api/users (ruta incompleta) ----------

    @Test
    @DisplayName("DELETE /api/users - sin email retorna 400 con mensaje guía")
    void eliminarSinEmail_retorna400() throws Exception {
        mockMvc.perform(delete("/api/users"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ruta incompleta"));
    }

    // ---------- GET /api/users/internal/auth-data ----------

    @Test
    @DisplayName("GET /api/users/internal/auth-data - retorna datos completos del usuario")
    void getAuthDataByEmail_existente_retorna200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setAutoEmail("jua.per@hospital.com");
        user.setContrasena("HASHED");

        when(userService.findByEmail("jua.per@hospital.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/internal/auth-data").param("email", "jua.per@hospital.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autoEmail").value("jua.per@hospital.com"));
    }

    @Test
    @DisplayName("GET /api/users/internal/auth-data - usuario inexistente retorna 400")
    void getAuthDataByEmail_inexistente_retorna400() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/internal/auth-data").param("email", "noexiste@hospital.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Error"));
    }
}