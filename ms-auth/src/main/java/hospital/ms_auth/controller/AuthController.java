package hospital.ms_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hospital.ms_auth.dto.LoginRequestDTO;
import hospital.ms_auth.dto.AuthResponseDTO;
import hospital.ms_auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Módulo de Seguridad (Autenticación)", description = "Controlador centralizado para la verificación de identidad del Hospital y emisión de credenciales seguras.")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión en el Hospital (Emitir JWT)",
        description = "Recibe el correo institucional del empleado y su clave secreta. Si el usuario existe y está activo, genera un token Bearer JWT firmado para interactuar con los demás microservicios."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticación exitosa. Se devuelve el token JWT estructurado.",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Error en el formato de la petición (correo inválido o campos vacíos).", content = @Content),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario suspendido administrativamente (active: false).", content = @Content)
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {

        String email = loginDTO.getEmail();
        String contrasena = loginDTO.getContrasena();

        // El AuthService procesa las credenciales
        String token = authService.login(email, contrasena);

        // Retornamos el DTO estructurado en lugar del viejo mapa genérico
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}