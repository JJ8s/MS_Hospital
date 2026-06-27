package hospital.ms_facturacion.dto;

import lombok.Data;

@Data
public class RecetaDTO {
    private Long id;
    private Long pacienteId;
    private Long productoId;
}