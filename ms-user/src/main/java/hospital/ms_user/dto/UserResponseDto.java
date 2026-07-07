package hospital.ms_user.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDto {
    private Long id;
    private String autoEmail; // Aquí verá su correo autogenerado
    private String nombre;
    private String apellido;
    private String telefono;
    private boolean active;
    private Set<String> roles; // Formato: ["MEDICO"] o ["PACIENTE"]
}