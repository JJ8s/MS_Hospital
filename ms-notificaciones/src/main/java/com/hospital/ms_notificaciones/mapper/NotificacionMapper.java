package com.hospital.ms_notificaciones.mapper;

import com.hospital.ms_notificaciones.dto.request.NotificacionRequestDTO;
import com.hospital.ms_notificaciones.dto.response.NotificacionResponseDTO;
import com.hospital.ms_notificaciones.model.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {

    public Notificacion toEntity(NotificacionRequestDTO dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatarioId(dto.getDestinatarioId());
        notificacion.setTipoDestinatario(dto.getTipoDestinatario());
        notificacion.setCanal(dto.getCanal());
        notificacion.setAsunto(dto.getAsunto());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setEstado("PENDIENTE");
        return notificacion;
    }

    public NotificacionResponseDTO toResponse(Notificacion notificacion) {
        return new NotificacionResponseDTO(
                notificacion.getId(),
                notificacion.getDestinatarioId(),
                notificacion.getTipoDestinatario(),
                notificacion.getCanal(),
                notificacion.getAsunto(),
                notificacion.getMensaje(),
                notificacion.getEstado(),
                notificacion.getFechaCreacion(),
                notificacion.getFechaEnvio()
        );
    }
}
