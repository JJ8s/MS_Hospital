package hospital.ms_user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String autoEmail;
    private String nombre;
    private String apellido;
    private String telefono;
    private String role;
    private boolean active;
}