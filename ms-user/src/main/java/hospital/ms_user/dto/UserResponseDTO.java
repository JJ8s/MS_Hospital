package hospital.ms_user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Representación segura de los datos de un usuario devuelta por la API")
public class UserResponseDTO {

    @Schema(description = "Identificador único asignado automáticamente por la base de datos", example = "3")
    private Long id;

    @Schema(description = "Correo institucional generado de forma automática por la lógica de negocio", example = "admin.prueba@hospital.com")
    private String autoEmail;

    @Schema(description = "Nombre(s) del usuario", example = "admin")
    private String nombre;

    @Schema(description = "Apellido(s) del usuario", example = "prueba")
    private String apellido;

    @Schema(description = "Número telefónico de contacto registrado", example = "144445555")
    private String telefono;

    @Schema(description = "Rol actual asignado al usuario", example = "ROLE_ADMIN")
    private String role;

    @Schema(description = "Estado lógico del usuario (Activo/Inactivo)", example = "true")
    private boolean active;
}