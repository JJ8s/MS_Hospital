package hospital.ms_user.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserCreateDTO {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Email(message = "Debe ser un correo electrónico válido")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @NotBlank(message = "El rol es obligatorio")
    private String role; // Ejemplo: ADMIN, DOCTOR, PATIENT
}

