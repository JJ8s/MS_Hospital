package hospital.ms_facturacion.service;

import hospital.ms_facturacion.client.RecetaClient;
import hospital.ms_facturacion.client.InventarioClient; 
import hospital.ms_facturacion.model.Factura;
import hospital.ms_facturacion.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private RecetaClient recetaClient;

    @Autowired
    private InventarioClient inventarioClient;

    public List<Factura> listarTodas() {
        return facturaRepository.findAll();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Factura crearFactura(Factura factura) {
        
        if (facturaRepository.findByRecetaId(factura.getRecetaId()).isPresent()) {
            throw new RuntimeException("Error: Ya existe una factura emitida para la receta No. " + factura.getRecetaId());
        }

        try {
            
            Map<String, Object> receta = (Map<String, Object>) recetaClient.obtenerPorId(factura.getRecetaId());
            Long productoId = Long.valueOf(receta.get("productoId").toString());
            Long pacienteId = Long.valueOf(receta.get("pacienteId").toString());

            
            Map<String, Object> producto = (Map<String, Object>) inventarioClient.obtenerPorId(productoId);
            Double precioProducto = Double.valueOf(producto.get("precio").toString());

            
            factura.setPacienteId(pacienteId);
            
            
            Double montoFinal = factura.getCostoServicio() + precioProducto;
            factura.setMontoTotal(montoFinal); 

            factura.setEstado("PENDIENTE");

            return facturaRepository.save(factura);

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar facturación: " + e.getMessage());
        }
    }

    @Transactional
    public Factura pagarFactura(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Factura No. " + id + " no encontrada."));
        
        if ("PAGADA".equals(factura.getEstado())) {
            throw new RuntimeException("La factura ya se encuentra pagada.");
        }
        
        factura.setEstado("PAGADA");
        return facturaRepository.save(factura);
    }
}