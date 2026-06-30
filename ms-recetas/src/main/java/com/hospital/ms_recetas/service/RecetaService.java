package com.hospital.ms_recetas.service;

import com.hospital.ms_recetas.client.ProductoClient;
import com.hospital.ms_recetas.dto.request.AjusteStockRequestDTO;
import com.hospital.ms_recetas.dto.request.RecetaRequestDTO;
import com.hospital.ms_recetas.dto.request.RecetaUpdateDTO;
import com.hospital.ms_recetas.dto.response.RecetaResponseDTO;
import com.hospital.ms_recetas.exception.IntegracionInventarioException;
import com.hospital.ms_recetas.exception.ResourceNotFoundException;
import com.hospital.ms_recetas.mapper.RecetaMapper;
import com.hospital.ms_recetas.model.Receta;
import com.hospital.ms_recetas.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final ProductoClient productoClient;
    private final RecetaMapper recetaMapper;

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarTodas() {
        return recetaRepository.findAll()
                .stream()
                .map(recetaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecetaResponseDTO obtenerPorId(Long id) {
        return recetaMapper.toResponse(obtenerEntidadPorId(id));
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> obtenerPorPaciente(Long pacienteId) {
        List<Receta> recetas = recetaRepository.findByPacienteId(pacienteId);
        if (recetas.isEmpty()) {
            throw new ResourceNotFoundException("El paciente #" + pacienteId + " no tiene historial de recetas.");
        }
        return recetas.stream().map(recetaMapper::toResponse).toList();
    }

    @Transactional
    public RecetaResponseDTO guardar(RecetaRequestDTO request) {
        validarIntegridadReceta(request);
        descontarStock(request.getProductoId(), request.getCantidad());

        Receta receta = recetaMapper.toEntity(request);
        return recetaMapper.toResponse(recetaRepository.save(receta));
    }

    @Transactional
    public void eliminar(Long id) {
        Receta receta = obtenerEntidadPorId(id);
        reponerStock(receta.getProductoId(), receta.getCantidad());
        recetaRepository.deleteById(id);
    }

    @Transactional
    public RecetaResponseDTO actualizarIndicaciones(Long id, RecetaUpdateDTO request) {
        Receta receta = obtenerEntidadPorId(id);
        recetaMapper.updateIndicaciones(receta, request);
        return recetaMapper.toResponse(recetaRepository.save(receta));
    }

    private Receta obtenerEntidadPorId(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la receta con ID " + id));
    }

    private void validarIntegridadReceta(RecetaRequestDTO request) {
        if (request.getPacienteId() == null) {
            throw new IllegalArgumentException("El ID del paciente es obligatorio.");
        }
        if (request.getProductoId() == null) {
            throw new IllegalArgumentException("Debe especificar un producto del inventario.");
        }
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad recetada debe ser mayor a cero.");
        }
    }

    private void descontarStock(Long productoId, Integer cantidad) {
        try {
            productoClient.reducirStock(productoId, new AjusteStockRequestDTO(cantidad));
        } catch (Exception ex) {
            throw new IntegracionInventarioException("No se pudo descontar stock del producto #" + productoId
                    + ". Verifica ms-inventario, el token y el stock disponible.");
        }
    }

    private void reponerStock(Long productoId, Integer cantidad) {
        try {
            productoClient.reponerStock(productoId, new AjusteStockRequestDTO(cantidad));
        } catch (Exception ex) {
            throw new IntegracionInventarioException("No se pudo reponer stock del producto #" + productoId
                    + ". Verifica ms-inventario y el token JWT.");
        }
    }
}
