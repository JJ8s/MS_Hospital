package com.hospital.ms_facturacion.mapper;

import com.hospital.ms_facturacion.dto.request.FacturaRequestDTO;
import com.hospital.ms_facturacion.dto.response.FacturaResponseDTO;
import com.hospital.ms_facturacion.model.Factura;
import org.springframework.stereotype.Component;

@Component
public class FacturaMapper {

    public Factura toEntity(FacturaRequestDTO dto) {
        Factura factura = new Factura();
        factura.setRecetaId(dto.getRecetaId());
        factura.setCostoServicio(dto.getCostoServicio());
        return factura;
    }

    public FacturaResponseDTO toResponse(Factura factura) {
        return new FacturaResponseDTO(
                factura.getId(),
                factura.getRecetaId(),
                factura.getPacienteId(),
                factura.getCostoServicio(),
                factura.getMontoTotal(),
                factura.getEstado(),
                factura.getFechaEmision()
        );
    }
}
