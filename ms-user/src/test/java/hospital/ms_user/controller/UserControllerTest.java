package hospital.ms_user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hospital.ms_user.model.User;
import hospital.ms_user.service.UserService;
import hospital.ms_user.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RolRepository rolRepository; 

    @Test
    void cuandoGetUsuarioPorId_yExiste_debeRetornarOk() throws Exception {
        
        Long id = 1L;
        User usuario = new User();
        usuario.setId(id);
        usuario.setNombre("Anderson");
        usuario.setAutoEmail("and.ovando@hospital.com");

        when(userService.findById(id)).thenReturn(Optional.of(usuario));

        
        mockMvc.perform(get("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Anderson"));

        verify(userService, times(1)).findById(id);
    }

    @Test
    void cuandoEliminarPorEmail_debeRetornarOk() throws Exception {
        
        String email = "admin.prueba@hospital.com";
        doNothing().when(userService).deleteByEmail(email);

        
        mockMvc.perform(delete("/api/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.mensaje").value("usuario admin.prueba@hospital.com eliminado")); 

        verify(userService, times(1)).deleteByEmail(email);
    }

    @Test
    void cuandoGetAllUsers_debeRetornarListaVacia() throws Exception {
        
        when(userService.findAll()).thenReturn(java.util.Collections.emptyList());

        
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).findAll();
    }

    @Test
    void cuandoGetUsuarioPorId_yNoExiste_debeRetornarNotFound() throws Exception {
        
        Long id = 999L;
        when(userService.findById(id)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(id);
    }

    @Test
    void cuandoGetUsuarioPorEmail_yExiste_debeRetornarOk() throws Exception {
        
        String email = "and.ovando@hospital.com";
        User usuario = new User();
        usuario.setAutoEmail(email);
        usuario.setNombre("Anderson");

        when(userService.findByEmail(email)).thenReturn(Optional.of(usuario));

        
        mockMvc.perform(get("/api/users/search")
                .param("autoemail", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autoEmail").value(email));

        verify(userService, times(1)).findByEmail(email);
    }

    @Test
    void cuandoCreateUser_yDatosValidos_debeRetornarCreated() throws Exception {
        
        String jsonPayload = "{\"nombre\":\"Anderson\",\"apellido\":\"Ovando\",\"telefono\":\"912345678\",\"contrasena\":\"clave123\",\"role\":\"USER\"}";
        
        hospital.ms_user.model.Rol rolFicticio = new hospital.ms_user.model.Rol();
        rolFicticio.setName("ROLE_USER");
        
        User usuarioGuardado = new User();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombre("Anderson");
        usuarioGuardado.setAutoEmail("and.ovando@hospital.com");

        when(rolRepository.findByName("ROLE_USER")).thenReturn(Optional.of(rolFicticio));
        when(userService.save(any(User.class))).thenReturn(usuarioGuardado);

        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Anderson"))
                .andExpect(jsonPath("$.autoEmail").value("and.ovando@hospital.com"));
    }

    @Test
    void cuandoGetUsuarioPorEmail_yNoExiste_debeRetornarNotFound() throws Exception {
        
        String email = "inventado@hospital.com";
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/users/search")
                .param("autoemail", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByEmail(email);
    }

    @Test
    void cuandoEliminarSinEmail_debeRetornarBadRequestYAdvertencia() throws Exception {
        
        mockMvc.perform(delete("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ruta incompleta"))
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void cuandoCreateUser_yRolNoExiste_debeRetornarBadRequest() throws Exception {
        
        String jsonPayload = "{\"nombre\":\"Anderson\",\"apellido\":\"Ovando\",\"telefono\":\"912345678\",\"contrasena\":\"clave123\",\"role\":\"ROL_INVENTADO\"}";
        
        
        when(rolRepository.findByName("ROLE_ROL_INVENTADO")).thenReturn(Optional.empty());

        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void cuandoCreateUser_yRolYaTienePrefijoRole_debeProcesarCorrectamente() throws Exception {
        
        
        String jsonPayload = "{\"nombre\":\"Anderson\",\"apellido\":\"Ovando\",\"telefono\":\"912345678\",\"contrasena\":\"clave123\",\"role\":\"ROLE_USER\"}";
        
        hospital.ms_user.model.Rol rolFicticio = new hospital.ms_user.model.Rol();
        rolFicticio.setName("ROLE_USER");
        
        User usuarioGuardado = new User();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombre("Anderson");
        usuarioGuardado.setAutoEmail("and.ovando@hospital.com");

        when(rolRepository.findByName("ROLE_USER")).thenReturn(Optional.of(rolFicticio));
        when(userService.save(any(User.class))).thenReturn(usuarioGuardado);

        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated());
    }



    
}