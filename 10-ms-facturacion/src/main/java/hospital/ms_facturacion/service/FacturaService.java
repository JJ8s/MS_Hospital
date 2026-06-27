package hospital.ms_facturacion.service;

import hospital.ms_facturacion.client.RecetaClient;
import hospital.ms_facturacion.client.InventarioClient; 
import hospital.ms_facturacion.model.Factura;
import hospital.ms_facturacion.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional(readOnly = true)
    public List<Factura> listarTodas() {
        return facturaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Factura obtenerPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Factura No. " + id + " no encontrada."));
    }

    @Transactional
    public Factura crearFactura(Factura factura) {
       
        if (facturaRepository.findByRecetaId(factura.getRecetaId()).isPresent()) {
            throw new RuntimeException("Error: Ya existe una factura emitida para la receta No. " + factura.getRecetaId());
        }

        try {
            
            Map<String, Object> receta = recetaClient.obtenerPorId(factura.getRecetaId()).getBody();
            
            if (receta == null) throw new RuntimeException("La receta no devolvió datos.");

            Long productoId = Long.valueOf(receta.get("productoId").toString());
            Long pacienteId = Long.valueOf(receta.get("pacienteId").toString());

            
            Map<String, Object> producto = inventarioClient.obtenerPorId(productoId).getBody();
            
            if (producto == null) throw new RuntimeException("El producto no devolvió datos de precio.");
            
            Double precioProducto = Double.valueOf(producto.get("precio").toString());

            
            factura.setPacienteId(pacienteId);
            Double montoFinal = factura.getCostoServicio() + precioProducto;
            factura.setMontoTotal(montoFinal); 
            factura.setEstado("PENDIENTE");
            factura.setFechaEmision(LocalDateTime.now());

            return facturaRepository.save(factura);

        } catch (Exception e) {
           
            throw new RuntimeException("Fallo en Interoperabilidad: " + e.getMessage());
        }
    }

    @Transactional
    public Factura pagarFactura(Long id) {
        Factura factura = obtenerPorId(id);
        
        if ("PAGADA".equals(factura.getEstado())) {
            throw new RuntimeException("Validación: La factura ya se encuentra pagada.");
        }
        
        factura.setEstado("PAGADA");
        return facturaRepository.save(factura);
    }

    
    @Transactional
    public Factura actualizarFactura(Long id, Factura detalles) {
        Factura facturaExistente = obtenerPorId(id);

        if ("PAGADA".equals(facturaExistente.getEstado())) {
            throw new RuntimeException("Seguridad: No se puede modificar una factura con estado PAGADA.");
        }

        // No se permite cambiar la receta original para evitar inconsistencias de cobro
        facturaExistente.setCostoServicio(detalles.getCostoServicio());
        
        // Recalcular el monto total basándose en el nuevo costo de servicio
        // (Se asume que el precio del producto se mantiene desde la creación original)
        Double precioProductoOriginal = facturaExistente.getMontoTotal() - facturaExistente.getCostoServicio();
        facturaExistente.setMontoTotal(detalles.getCostoServicio() + precioProductoOriginal);

        return facturaRepository.save(facturaExistente);
    }

    
    @Transactional
    public void eliminarFactura(Long id) {
        Factura factura = obtenerPorId(id);
        
        if ("PAGADA".equals(factura.getEstado())) {
            throw new RuntimeException("Auditoría: Está prohibido eliminar facturas pagadas del historial financiero.");
        }
        
        facturaRepository.delete(factura);
    }
}