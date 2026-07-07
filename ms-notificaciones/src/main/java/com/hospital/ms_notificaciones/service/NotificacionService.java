package com.hospital.ms_notificaciones.service;

import com.hospital.ms_notificaciones.dto.request.NotificacionRequestDTO;
import com.hospital.ms_notificaciones.dto.response.NotificacionResponseDTO;
import com.hospital.ms_notificaciones.exception.EstadoNotificacionException;
import com.hospital.ms_notificaciones.exception.ResourceNotFoundException;
import com.hospital.ms_notificaciones.mapper.NotificacionMapper;
import com.hospital.ms_notificaciones.model.Notificacion;
import com.hospital.ms_notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarTodas() {
        return notificacionRepository.findAll()
                .stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificacionResponseDTO obtenerPorId(Long id) {
        return notificacionMapper.toResponse(obtenerEntidadPorId(id));
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorDestinatario(Long destinatarioId) {
        return notificacionRepository.findByDestinatarioId(destinatarioId)
                .stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorEstado(String estado) {
        return notificacionRepository.findByEstado(estado)
                .stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }

    @Transactional
    public NotificacionResponseDTO crear(NotificacionRequestDTO request) {
        Notificacion notificacion = notificacionMapper.toEntity(request);
        return notificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }

    @Transactional
    public NotificacionResponseDTO marcarComoEnviada(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);

        if ("LEIDA".equals(notificacion.getEstado())) {
            throw new EstadoNotificacionException("No se puede enviar una notificacion que ya fue leida.");
        }

        notificacion.setEstado("ENVIADA");
        notificacion.setFechaEnvio(LocalDateTime.now());
        return notificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }

    @Transactional
    public NotificacionResponseDTO marcarComoLeida(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);

        if ("PENDIENTE".equals(notificacion.getEstado())) {
            throw new EstadoNotificacionException("No se puede marcar como leida una notificacion pendiente de envio.");
        }

        notificacion.setEstado("LEIDA");
        return notificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }

    @Transactional
    public void eliminar(Long id) {
        Notificacion notificacion = obtenerEntidadPorId(id);
        notificacionRepository.delete(notificacion);
    }

    private Notificacion obtenerEntidadPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la notificacion con ID " + id));
    }
}
