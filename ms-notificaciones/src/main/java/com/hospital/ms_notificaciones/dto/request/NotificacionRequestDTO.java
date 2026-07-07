package com.hospital.ms_notificaciones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear una notificacion")
public class NotificacionRequestDTO {

    @NotNull(message = "El ID del destinatario es obligatorio")
    @Schema(example = "1")
    private Long destinatarioId;

    @NotBlank(message = "El tipo de destinatario es obligatorio")
    @Pattern(regexp = "PACIENTE|MEDICO|ADMIN", message = "El tipo debe ser PACIENTE, MEDICO o ADMIN")
    @Schema(example = "PACIENTE")
    private String tipoDestinatario;

    @NotBlank(message = "El canal es obligatorio")
    @Pattern(regexp = "EMAIL|SMS|APP", message = "El canal debe ser EMAIL, SMS o APP")
    @Schema(example = "APP")
    private String canal;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 3, max = 120, message = "El asunto debe tener entre 3 y 120 caracteres")
    @Schema(example = "Recordatorio de cita")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 5, max = 500, message = "El mensaje debe tener entre 5 y 500 caracteres")
    @Schema(example = "Tiene una cita medica agendada para manana a las 10:30.")
    private String mensaje;
}
