package com.hospital.ms_notificaciones.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con datos de la notificacion")
public class NotificacionResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "1")
    private Long destinatarioId;

    @Schema(example = "PACIENTE")
    private String tipoDestinatario;

    @Schema(example = "APP")
    private String canal;

    @Schema(example = "Recordatorio de cita")
    private String asunto;

    @Schema(example = "Tiene una cita medica agendada para manana a las 10:30.")
    private String mensaje;

    @Schema(example = "PENDIENTE")
    private String estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaEnvio;
}
