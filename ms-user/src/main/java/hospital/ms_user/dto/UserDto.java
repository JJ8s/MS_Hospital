package hospital.ms_user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(min = 9, message = "Mínimo 9 dígitos")
    private String telefono;

    // Aquí el usuario asigna la contraseña para su nueva cuenta/correo
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @NotEmpty(message = "Debe asignar al menos un rol (ADMIN, MEDICO o PACIENTE)")
    private List<String> roles;
}