package com.hospital.ms_recetas.mapper;

import com.hospital.ms_recetas.dto.request.RecetaRequestDTO;
import com.hospital.ms_recetas.dto.request.RecetaUpdateDTO;
import com.hospital.ms_recetas.dto.response.RecetaResponseDTO;
import com.hospital.ms_recetas.model.Receta;
import org.springframework.stereotype.Component;

@Component
public class RecetaMapper {

    public Receta toEntity(RecetaRequestDTO dto) {
        Receta receta = new Receta();
        receta.setPacienteId(dto.getPacienteId());
        receta.setProductoId(dto.getProductoId());
        receta.setCantidad(dto.getCantidad());
        receta.setIndicaciones(dto.getIndicaciones());
        receta.setDoctorResponsable(dto.getDoctorResponsable());
        return receta;
    }

    public RecetaResponseDTO toResponse(Receta receta) {
        return new RecetaResponseDTO(
                receta.getId(),
                receta.getPacienteId(),
                receta.getProductoId(),
                receta.getCantidad(),
                receta.getIndicaciones(),
                receta.getDoctorResponsable(),
                receta.getFechaEmision()
        );
    }

    public void updateIndicaciones(Receta receta, RecetaUpdateDTO dto) {
        receta.setIndicaciones(dto.getIndicaciones());
        receta.setDoctorResponsable(dto.getDoctorResponsable());
    }
}
