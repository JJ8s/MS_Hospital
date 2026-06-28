package hospital.ms_user.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String autoEmail; // Identificador generado
    private String nombre;
    private String apellido;
    private String telefono;
    private String role;
    private boolean active;
}