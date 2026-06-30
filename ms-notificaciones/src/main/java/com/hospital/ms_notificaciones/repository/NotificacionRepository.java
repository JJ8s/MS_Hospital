package com.hospital.ms_notificaciones.repository;

import com.hospital.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatarioId(Long destinatarioId);

    List<Notificacion> findByEstado(String estado);
}
