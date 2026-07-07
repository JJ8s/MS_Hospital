package hospital.ms_user.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.dto.UserResponseDTO;
import hospital.ms_user.exception.BadRequestException;
import hospital.ms_user.mapper.UserMapper;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;
import hospital.ms_user.repository.RolRepository;
import hospital.ms_user.service.UserService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Controlador principal para la gestión y administración de usuarios del hospital")
public class UserController {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Autowired
    private UserService userService;

    @Autowired
    private RolRepository rolRepository; 

    @Operation(summary = "Obtener todos los usuarios", description = "Recupera una lista completa con todos los usuarios registrados en el sistema transformados a DTO seguro.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida con éxito",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    })
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener un usuario por su ID", description = "Busca un usuario específico en la base de datos utilizando su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado con éxito",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "El usuario con el ID especificado no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario a consultar", example = "1") 
            @PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar usuario por correo", description = "Recupera los datos de un usuario utilizando su correo institucional autogenerado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado con éxito",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró ningún usuario con el correo proporcionado", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Correo institucional del usuario", example = "admin.prueba@hospital.com") 
            @RequestParam String autoemail) {
        return userService.findByEmail(autoemail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en el sistema. Valida el rol ingresado, cifra la contraseña y genera el correo institucional de forma automática.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o el Rol especificado no existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        User userEntity = new User();
        userEntity.setNombre(createDTO.getNombre());
        userEntity.setApellido(createDTO.getApellido());
        userEntity.setTelefono(createDTO.getTelefono());
        userEntity.setContrasena(createDTO.getContrasena());

        String nombreRolBuscado = createDTO.getRole().toUpperCase().trim();
        if (!nombreRolBuscado.startsWith("ROLE_")) {
            nombreRolBuscado = "ROLE_" + nombreRolBuscado;
        }

        Rol rolEncontrado = rolRepository.findByName(nombreRolBuscado)
            .orElseThrow(() -> new BadRequestException("El rol '" + createDTO.getRole() + "' no existe en el sistema."));

        Set<Rol> rolesUsuario = new HashSet<>();
        rolesUsuario.add(rolEncontrado);
        userEntity.setRoles(rolesUsuario);

        User savedUser = userService.save(userEntity);
        
        return new ResponseEntity<>(userMapper.toResponseDTO(savedUser), HttpStatus.CREATED);
    }

    

    @Hidden 
    @DeleteMapping(value = {"", "/"})
    public ResponseEntity<Map<String, String>> advertirRutaIncorrecta() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Ruta incompleta");
        respuesta.put("mensaje", "Señala un correo usando: http://localhost:8081/api/users/email/ejemplo@hospital.com");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @Operation(summary = "Eliminar un usuario por correo", description = "Elimina permanentemente a un usuario del sistema identificándolo mediante su correo electrónico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito de la plataforma",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": \"Success\", \"mensaje\": \"usuario j.perez@hospital.com eliminado\"}"))),
        @ApiResponse(responseCode = "404", description = "El correo no corresponde a ningún usuario registrado", content = @Content)
    })
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> eliminarPorEmail(
            @Parameter(description = "Correo del usuario que se desea eliminar", example = "admin.prueba@hospital.com") 
            @PathVariable String email) {
        userService.deleteByEmail(email);
        
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "Success");
        respuesta.put("mensaje", "usuario " + email + " eliminado");
        
        return ResponseEntity.ok(respuesta);
    }

}
