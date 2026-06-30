package hospital.ms_user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hospital.ms_user.dto.UsuarioCreateDTO;
import hospital.ms_user.dto.UsuarioResponseDTO;
import hospital.ms_user.model.Usuario;
import hospital.ms_user.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(
    name = "Usuarios",
    description = "Operaciones relacionadas con la gestión de usuarios del sistema hospitalario"
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados sin mostrar contraseñas.")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario según su identificador.")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /*
     * Endpoint importante para ms-auth.
     * ms-auth puede buscar el usuario por correo institucional generado.
     * Ejemplo:
     * GET /api/users/search?autoemail=adm.prueba@hospital.com
     */
    @GetMapping("/search")
    @Operation(
        summary = "Buscar usuario por correo institucional",
        description = "Busca un usuario por autoEmail. Este endpoint es usado por ms-auth para validar login."
    )
    public ResponseEntity<Usuario> buscarPorAutoEmail(@RequestParam String autoemail) {
        return usuarioService.obtenerEntidadPorAutoEmail(autoemail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un usuario, genera correo institucional, asigna rol y encripta contraseña.")
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioCreateDTO dto) {
        UsuarioResponseDTO usuarioCreado = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario por ID", description = "Elimina un usuario según su identificador.")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) {
        usuarioService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/email/{autoEmail}")
    @Operation(summary = "Eliminar usuario por correo", description = "Elimina un usuario usando su correo institucional generado.")
    public ResponseEntity<Map<String, String>> eliminarPorAutoEmail(@PathVariable String autoEmail) {
        usuarioService.eliminarPorAutoEmail(autoEmail);

        return ResponseEntity.ok(Map.of(
                "status", "Success",
                "mensaje", "Usuario " + autoEmail + " eliminado correctamente"
        ));
    }
}