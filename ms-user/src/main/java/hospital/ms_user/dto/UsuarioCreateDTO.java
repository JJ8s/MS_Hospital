package hospital.ms_user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 9, message = "El teléfono debe tener al menos 9 caracteres")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;
    /*
     * Rol opcional.
     * Ejemplos válidos:
     * ADMIN
     * ROLE_ADMIN
     * MEDICO
     * ROLE_MEDICO
     */
    private String role;
}