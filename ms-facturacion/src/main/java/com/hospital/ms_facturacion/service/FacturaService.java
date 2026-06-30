package com.hospital.ms_facturacion.service;

import com.hospital.ms_facturacion.client.InventarioClient;
import com.hospital.ms_facturacion.client.RecetaClient;
import com.hospital.ms_facturacion.dto.ProductoDTO;
import com.hospital.ms_facturacion.dto.RecetaDTO;
import com.hospital.ms_facturacion.dto.request.FacturaRequestDTO;
import com.hospital.ms_facturacion.dto.request.FacturaUpdateDTO;
import com.hospital.ms_facturacion.dto.response.FacturaResponseDTO;
import com.hospital.ms_facturacion.exception.EstadoFacturaException;
import com.hospital.ms_facturacion.exception.FacturaDuplicadaException;
import com.hospital.ms_facturacion.exception.IntegracionFacturacionException;
import com.hospital.ms_facturacion.exception.ResourceNotFoundException;
import com.hospital.ms_facturacion.mapper.FacturaMapper;
import com.hospital.ms_facturacion.model.Factura;
import com.hospital.ms_facturacion.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final RecetaClient recetaClient;
    private final InventarioClient inventarioClient;
    private final FacturaMapper facturaMapper;

    @Transactional(readOnly = true)
    public List<FacturaResponseDTO> listarTodas() {
        return facturaRepository.findAll()
                .stream()
                .map(facturaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerPorId(Long id) {
        return facturaMapper.toResponse(obtenerEntidadPorId(id));
    }

    @Transactional(readOnly = true)
    public List<FacturaResponseDTO> obtenerPorPaciente(Long pacienteId) {
        return facturaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(facturaMapper::toResponse)
                .toList();
    }

    @Transactional
    public FacturaResponseDTO crearFactura(FacturaRequestDTO request) {
        validarRequest(request);

        if (facturaRepository.findByRecetaId(request.getRecetaId()).isPresent()) {
            throw new FacturaDuplicadaException("Ya existe una factura emitida para la receta No. "
                    + request.getRecetaId());
        }

        RecetaDTO receta = obtenerReceta(request.getRecetaId());
        ProductoDTO producto = obtenerProducto(receta.getProductoId());

        Factura factura = facturaMapper.toEntity(request);
        factura.setPacienteId(receta.getPacienteId());
        factura.setMontoTotal(request.getCostoServicio() + producto.getPrecio());
        factura.setEstado("PENDIENTE");
        factura.setFechaEmision(LocalDateTime.now());

        return facturaMapper.toResponse(facturaRepository.save(factura));
    }

    @Transactional
    public FacturaResponseDTO pagarFactura(Long id) {
        Factura factura = obtenerEntidadPorId(id);

        if ("PAGADA".equals(factura.getEstado())) {
            throw new EstadoFacturaException("La factura ya se encuentra pagada.");
        }
        if ("ANULADA".equals(factura.getEstado())) {
            throw new EstadoFacturaException("No se puede pagar una factura anulada.");
        }

        factura.setEstado("PAGADA");
        return facturaMapper.toResponse(facturaRepository.save(factura));
    }

    @Transactional
    public FacturaResponseDTO actualizarFactura(Long id, FacturaUpdateDTO request) {
        Factura factura = obtenerEntidadPorId(id);

        if ("PAGADA".equals(factura.getEstado())) {
            throw new EstadoFacturaException("No se puede modificar una factura con estado PAGADA.");
        }
        if ("ANULADA".equals(factura.getEstado())) {
            throw new EstadoFacturaException("No se puede modificar una factura anulada.");
        }

        Double precioProductoOriginal = factura.getMontoTotal() - factura.getCostoServicio();
        factura.setCostoServicio(request.getCostoServicio());
        factura.setMontoTotal(request.getCostoServicio() + precioProductoOriginal);

        return facturaMapper.toResponse(facturaRepository.save(factura));
    }

    @Transactional
    public void eliminarFactura(Long id) {
        Factura factura = obtenerEntidadPorId(id);

        if ("PAGADA".equals(factura.getEstado())) {
            throw new EstadoFacturaException("Esta prohibido eliminar facturas pagadas del historial financiero.");
        }

        facturaRepository.delete(factura);
    }

    private Factura obtenerEntidadPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la factura con ID " + id));
    }

    private void validarRequest(FacturaRequestDTO request) {
        if (request.getRecetaId() == null) {
            throw new IllegalArgumentException("El ID de la receta es obligatorio.");
        }
        if (request.getCostoServicio() == null || request.getCostoServicio() < 0) {
            throw new IllegalArgumentException("El costo de servicio debe ser mayor o igual a cero.");
        }
    }

    private RecetaDTO obtenerReceta(Long recetaId) {
        try {
            ResponseEntity<RecetaDTO> response = recetaClient.obtenerPorId(recetaId);
            RecetaDTO receta = response.getBody();
            if (receta == null || receta.getProductoId() == null || receta.getPacienteId() == null) {
                throw new IntegracionFacturacionException("La receta no devolvio datos validos.");
            }
            return receta;
        } catch (IntegracionFacturacionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IntegracionFacturacionException("No se pudo obtener la receta #" + recetaId
                    + ". Verifica ms-recetas y el token JWT.");
        }
    }

    private ProductoDTO obtenerProducto(Long productoId) {
        try {
            ResponseEntity<ProductoDTO> response = inventarioClient.obtenerPorId(productoId);
            ProductoDTO producto = response.getBody();
            if (producto == null || producto.getPrecio() == null) {
                throw new IntegracionFacturacionException("El producto no devolvio datos validos.");
            }
            return producto;
        } catch (IntegracionFacturacionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IntegracionFacturacionException("No se pudo obtener el producto #" + productoId
                    + ". Verifica ms-inventario y el token JWT.");
        }
    }
}
