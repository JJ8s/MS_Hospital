package com.hospital.ms_inventario.mapper;

import com.hospital.ms_inventario.dto.request.ProductoRequestDTO;
import com.hospital.ms_inventario.dto.response.ProductoResponseDTO;
import com.hospital.ms_inventario.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setLote(dto.getLote());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        return producto;
    }

    public ProductoResponseDTO toResponse(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getLote(),
                producto.getFechaVencimiento()
        );
    }

    public void updateEntity(Producto producto, ProductoRequestDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setLote(dto.getLote());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
    }
}
