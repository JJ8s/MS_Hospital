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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private ProductoClient productoClient;

    @Spy
    private RecetaMapper recetaMapper;

    @InjectMocks
    private RecetaService recetaService;

    @Test
    void listarTodas_retornaRecetas() {
        when(recetaRepository.findAll()).thenReturn(List.of(receta()));

        List<RecetaResponseDTO> resultado = recetaService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getPacienteId());
        verify(recetaRepository, times(1)).findAll();
    }

    @Test
    void guardar_validaYDescuentaStock() {
        RecetaRequestDTO request = request();
        Receta guardada = receta();

        doNothing().when(productoClient).reducirStock(eq(5L), any(AjusteStockRequestDTO.class));
        when(recetaRepository.save(any(Receta.class))).thenReturn(guardada);

        RecetaResponseDTO resultado = recetaService.guardar(request);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getPacienteId());
        verify(productoClient, times(1)).reducirStock(eq(5L), any(AjusteStockRequestDTO.class));
        verify(recetaRepository, times(1)).save(any(Receta.class));
    }

    @Test
    void guardar_conCantidadInvalida_lanzaExcepcion() {
        RecetaRequestDTO request = request();
        request.setCantidad(0);

        assertThrows(IllegalArgumentException.class, () -> recetaService.guardar(request));
        verify(productoClient, never()).reducirStock(any(), any());
        verify(recetaRepository, never()).save(any(Receta.class));
    }

    @Test
    void guardar_siInventarioFalla_lanzaIntegracionException() {
        RecetaRequestDTO request = request();
        doThrow(new RuntimeException("sin stock"))
                .when(productoClient).reducirStock(eq(5L), any(AjusteStockRequestDTO.class));

        assertThrows(IntegracionInventarioException.class, () -> recetaService.guardar(request));
        verify(recetaRepository, never()).save(any(Receta.class));
    }

    @Test
    void obtenerPorId_existente_retornaReceta() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta()));

        RecetaResponseDTO resultado = recetaService.obtenerPorId(1L);

        assertEquals(1L, resultado.getId());
        verify(recetaRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(recetaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recetaService.obtenerPorId(99L));
    }

    @Test
    void obtenerPorPaciente_sinRecetas_lanzaExcepcion() {
        when(recetaRepository.findByPacienteId(100L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> recetaService.obtenerPorPaciente(100L));
    }

    @Test
    void eliminar_existenteReponeStockYElimina() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta()));
        doNothing().when(productoClient).reponerStock(eq(5L), any(AjusteStockRequestDTO.class));

        recetaService.eliminar(1L);

        verify(productoClient, times(1)).reponerStock(eq(5L), any(AjusteStockRequestDTO.class));
        verify(recetaRepository, times(1)).deleteById(1L);
    }

    @Test
    void actualizarIndicaciones_soloActualizaCamposPermitidos() {
        RecetaUpdateDTO update = new RecetaUpdateDTO();
        update.setIndicaciones("Tomar despues de cada comida");
        update.setDoctorResponsable("Dra. Ana Lopez");

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta()));
        when(recetaRepository.save(any(Receta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RecetaResponseDTO resultado = recetaService.actualizarIndicaciones(1L, update);

        assertEquals("Tomar despues de cada comida", resultado.getIndicaciones());
        assertEquals("Dra. Ana Lopez", resultado.getDoctorResponsable());
        assertEquals(5L, resultado.getProductoId());
    }

    private Receta receta() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setPacienteId(100L);
        receta.setProductoId(5L);
        receta.setCantidad(2);
        receta.setDoctorResponsable("Dra. Ana Lopez");
        receta.setIndicaciones("Tomar cada 12 horas");
        receta.setFechaEmision(LocalDateTime.now());
        return receta;
    }

    private RecetaRequestDTO request() {
        RecetaRequestDTO request = new RecetaRequestDTO();
        request.setPacienteId(100L);
        request.setProductoId(5L);
        request.setCantidad(2);
        request.setDoctorResponsable("Dra. Ana Lopez");
        request.setIndicaciones("Tomar cada 12 horas");
        return request;
    }
}
