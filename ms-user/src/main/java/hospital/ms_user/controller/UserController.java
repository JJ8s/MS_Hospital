package hospital.ms_user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hospital.ms_user.model.User;
import hospital.ms_user.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * El método de búsqueda ahora utiliza el correo institucional generado.
     * URL en Postman: GET /api/users/search?autoemail=nombre.apellido@hospital.com
     */
    @GetMapping("/search")
    public ResponseEntity<User> getUserByEmail(@RequestParam String autoemail) {
        return userService.findByEmail(autoemail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // El servicio se encargará de generar el autoEmail y encriptar la contrasena
        User savedUser = userService.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * 1. ADVERTENCIA: Intercepta peticiones DELETE incorrectas a la ruta base /api/users o /api/users/
     * para guiar al usuario sobre el formato correcto.
     */
    @DeleteMapping(value = {"", "/"})
    public ResponseEntity<Map<String, String>> advertirRutaIncorrecta() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Ruta incompleta");
        respuesta.put("mensaje", "Señala un correo usando:http://localhost:8080/api/users/email/ejemplo@hospital.com");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    /**
     * 2. BORRADO EXITOSO: Modificado de 204 No Content a 200 OK para poder enviar el cuerpo JSON
     * notificando el éxito del proceso.
     */
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> eliminarPorEmail(@PathVariable String email) {
        userService.deleteByEmail(email);
        
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "Success");
        respuesta.put("mensaje", "usuario " + email + " borrado");
        
        return ResponseEntity.ok(respuesta);
    }
}