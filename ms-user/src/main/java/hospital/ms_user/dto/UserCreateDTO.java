package hospital.ms_user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estructura de datos requerida para el registro de un nuevo usuario en el sistema")
public class UserCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre(s) del usuario", example = "Carlos Alfonso", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido(s) del usuario", example = "Gutiérrez Sanhueza", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(min = 9, message = "Mínimo 9 dígitos")
    @Schema(description = "Número telefónico de contacto (mínimo 9 dígitos)", example = "+56912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    @Schema(description = "Contraseña de acceso al sistema (mínimo 8 caracteres)", example = "Segura123*", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contrasena;

    @NotBlank(message = "El rol es obligatorio (ej: ADMIN, MEDICO, PACIENTE)") 
    @Schema(description = "Rol que desempeñará el usuario (Se le antepondrá 'ROLE_' de manera interna si no se incluye)", example = "MEDICO", allowableValues = {"ADMIN", "MEDICO", "PACIENTE"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String role; 
}