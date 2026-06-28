package hospital.ms_user.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.dto.UserResponseDTO;
import hospital.ms_user.mapper.UserMapper;
import hospital.ms_user.model.User;
import hospital.ms_user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios y personal del hospital")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista segura sin mostrar contraseñas")
    public List<UserResponseDTO> getAllUsers() {
        return userMapper.toResponseDTOs(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado con éxito")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        // Mejoramos para devolver un DTO seguro en lugar de la entidad completa
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toResponseDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar por correo automático", description = "Endpoint CRÍTICO para el microservicio ms-auth")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        // Cambiamos 'username' por 'email' para coincidir con la lógica de autoEmail
        return userService.findByAutoEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Genera automáticamente el correo institucional")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        // IMPORTANTE: Pasamos el DTO directamente al servicio para que ejecute 
        // la lógica de generación de correo institucional.
        User savedUser = userService.save(createDTO);
        return new ResponseEntity<>(userMapper.toResponseDTO(savedUser), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}