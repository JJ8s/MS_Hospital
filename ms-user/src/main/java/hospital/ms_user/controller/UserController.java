package hospital.ms_user.controller;

import hospital.ms_user.dto.UserDto;
import hospital.ms_user.dto.UserResponseDto;
import hospital.ms_user.model.User;
import hospital.ms_user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Inyección limpia por constructor (Evita usar @Autowired en atributos)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1. Registrar un nuevo usuario (POST)
     * Utiliza @Valid para activar las validaciones automáticas definidas en el UserDto.
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserResponseDto nuevoUsuario = userService.registrarUsuario(userDto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    /**
     * 2. Listar todos los usuarios activos (GET)
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> listarUsuarios() {
        return ResponseEntity.ok(userService.obtenerTodos());
    }

    /**
     * 3. Desactivación de usuario / Borrado lógico por Email (DELETE)
     * Cumple con los estándares hospitalarios de no eliminar historiales clínicos de la BD.
     */
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> eliminarPorEmail(@PathVariable String email) {
        userService.deleteByEmail(email); // Cambiará active = false en el Service

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "Success");
        respuesta.put("mensaje", "El usuario con correo '" + email + "' ha sido desactivado correctamente.");
        return ResponseEntity.ok(respuesta);
    }

    /**
     * 4. Contingencia para ruta DELETE incompleta
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> eliminarSinEmail() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Ruta incompleta");
        respuesta.put("mensaje", "Por favor, señala un correo usando: http://localhost:8081/api/users/email/ejemplo@hospital.com");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    /**
     * 5. Endpoint de comunicación interna para el Servidor de Seguridad (GET)
     * Devuelve la entidad completa (con contraseña encriptada) para que el Gateway / Auth valide el JWT.
     */
    @GetMapping("/internal/auth-data")
    public ResponseEntity<User> getAuthDataByEmail(@RequestParam("email") String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + email));
        return ResponseEntity.ok(user);
    }
}