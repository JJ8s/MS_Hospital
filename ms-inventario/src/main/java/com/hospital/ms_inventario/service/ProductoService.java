package com.hospital.ms_inventario.service;

import com.hospital.ms_inventario.dto.request.ProductoRequestDTO;
import com.hospital.ms_inventario.dto.response.ProductoResponseDTO;
import com.hospital.ms_inventario.exception.LoteDuplicadoException;
import com.hospital.ms_inventario.exception.ProductoDuplicadoException;
import com.hospital.ms_inventario.exception.ResourceNotFoundException;
import com.hospital.ms_inventario.exception.StockInsuficienteException;
import com.hospital.ms_inventario.mapper.ProductoMapper;
import com.hospital.ms_inventario.model.Producto;
import com.hospital.ms_inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = obtenerEntidadPorId(id);
        alertarSiVencePronto(producto);
        return productoMapper.toResponse(producto);
    }

    @Transactional
    public ProductoResponseDTO guardar(ProductoRequestDTO request) {
        validarNombreDisponible(request.getNombre(), null);
        validarLoteDisponible(request.getLote(), null);
        validarStockNoNegativo(request.getStock());

        Producto producto = productoMapper.toEntity(request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request) {
        Producto producto = obtenerEntidadPorId(id);

        validarNombreDisponible(request.getNombre(), id);
        validarLoteDisponible(request.getLote(), id);
        validarStockNoNegativo(request.getStock());

        productoMapper.updateEntity(producto, request);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO reducirStock(Long id, Integer cantidad) {
        validarCantidadPositiva(cantidad);

        Producto producto = obtenerEntidadPorId(id);
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente: solo quedan " + producto.getStock()
                    + " unidades de " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - cantidad);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO reponerStock(Long id, Integer cantidad) {
        validarCantidadPositiva(cantidad);

        Producto producto = obtenerEntidadPorId(id);
        producto.setStock(producto.getStock() + cantidad);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontro el producto con ID " + id);
        }
        productoRepository.deleteById(id);
    }

    private Producto obtenerEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el producto con ID " + id));
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        productoRepository.findByNombreIgnoreCase(nombre)
                .filter(producto -> !producto.getId().equals(idActual))
                .ifPresent(producto -> {
                    throw new ProductoDuplicadoException("El producto '" + nombre + "' ya esta registrado.");
                });
    }

    private void validarLoteDisponible(String lote, Long idActual) {
        productoRepository.findByLote(lote)
                .filter(producto -> !producto.getId().equals(idActual))
                .ifPresent(producto -> {
                    throw new LoteDuplicadoException("El lote '" + lote + "' ya existe en el sistema.");
                });
    }

    private void validarStockNoNegativo(Integer stock) {
        if (stock == null || stock < 0) {
            throw new StockInsuficienteException("El stock no puede ser negativo.");
        }
    }

    private void validarCantidadPositiva(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }

    private void alertarSiVencePronto(Producto producto) {
        if (producto.getFechaVencimiento() != null) {
            long diasParaVencer = ChronoUnit.DAYS.between(LocalDate.now(), producto.getFechaVencimiento());
            if (diasParaVencer <= 30 && diasParaVencer >= 0) {
                System.out.println("ALERTA SANITARIA: El producto " + producto.getNombre()
                        + " vence en " + diasParaVencer + " dias.");
            }
        }
    }
}
