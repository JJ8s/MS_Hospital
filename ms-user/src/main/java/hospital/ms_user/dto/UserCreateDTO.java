package hospital.ms_user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(min = 9, message = "Mínimo 9 dígitos")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    private String contrasena;

    @NotBlank(message = "El rol es obligatorio")
    private String role; // Ejemplo: ADMIN, DOCTOR

    public String generateSystemEmail() {
        if (this.nombre == null || this.apellido == null) return null;
        return (this.nombre + "." + this.apellido).toLowerCase().trim().replace(" ", ".") + "@hospital.com";
    }
    
}