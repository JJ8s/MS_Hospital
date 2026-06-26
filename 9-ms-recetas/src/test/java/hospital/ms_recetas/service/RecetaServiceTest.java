package hospital.ms_recetas.service;

import hospital.ms_recetas.client.ProductoClient;
import hospital.ms_recetas.model.Receta;
import hospital.ms_recetas.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private RecetaService recetaService;

    private Receta recetaValida;

    @BeforeEach
    void setUp() {
        recetaValida = new Receta();
        recetaValida.setId(1L);
        recetaValida.setPacienteId(100L);
        recetaValida.setProductoId(5L);
        recetaValida.setCantidad(2);
        recetaValida.setDoctorResponsable("Dra. Ana Lopez");
        recetaValida.setIndicaciones("Tomar cada 12 horas");
    }

    @Test
    void cuandoGuardarReceta_entoncesExitoYDescuentaStock() {
        // GIVEN
        doNothing().when(productoClient).reducirStock(5L, 2);
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaValida);

        // WHEN
        Receta resultado = recetaService.guardar(recetaValida);

        // THEN
        assertNotNull(resultado);
        assertEquals(100L, resultado.getPacienteId());
        verify(productoClient, times(1)).reducirStock(5L, 2);
        verify(recetaRepository, times(1)).save(any(Receta.class));
    }

    @Test
    void cuandoGuardarRecetaConCantidadInvalida_entoncesLanzaExcepcion() {
        // GIVEN
        recetaValida.setCantidad(-1);

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            recetaService.guardar(recetaValida);
        });

        assertTrue(exception.getMessage().contains("mayor a cero"));
        verify(productoClient, never()).reducirStock(anyLong(), anyInt());
        verify(recetaRepository, never()).save(any(Receta.class));
    }

    @Test
    void cuandoErrorEnComunicacionRemota_entoncesLanzaRuntimeException() {
        // GIVEN
        doThrow(new RuntimeException("Error de conexión")).when(productoClient).reducirStock(anyLong(), anyInt());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recetaService.guardar(recetaValida);
        });

        assertTrue(exception.getMessage().contains("Fallo en Interoperabilidad"));
        verify(recetaRepository, never()).save(any(Receta.class));
    }

    @Test
    void cuandoObtenerPorPacienteSinRecetas_entoncesLanzaExcepcion() {
        // GIVEN
        when(recetaRepository.findByPacienteId(100L)).thenReturn(new ArrayList<>());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recetaService.obtenerPorPaciente(100L);
        });

        assertEquals("Consulta: El paciente #100 no tiene historial de recetas.", exception.getMessage());
    }

    @Test
    void cuandoEliminarReceta_entoncesReponeStockYBorra() {
        // GIVEN
        Long idEliminar = 1L;
        when(recetaRepository.findById(idEliminar)).thenReturn(Optional.of(recetaValida));
        doNothing().when(productoClient).reponerStock(5L, 2);
        doNothing().when(recetaRepository).deleteById(idEliminar);

        // WHEN
        recetaService.eliminar(idEliminar);

        // THEN
        verify(productoClient, times(1)).reponerStock(5L, 2);
        verify(recetaRepository, times(1)).deleteById(idEliminar);
    }

    @Test
    void cuandoActualizarIndicaciones_entoncesSoloCambiaCamposPermitidos() {
        // GIVEN
        Receta nuevosDetalles = new Receta();
        nuevosDetalles.setIndicaciones("Nueva indicación");
        nuevosDetalles.setDoctorResponsable("Dr. Update");

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(recetaValida));
        when(recetaRepository.save(any(Receta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Receta resultado = recetaService.actualizarIndicaciones(1L, nuevosDetalles);

        // THEN
        assertEquals("Nueva indicación", resultado.getIndicaciones());
        assertEquals("Dr. Update", resultado.getDoctorResponsable());
        // Verificamos que el producto original no cambió por seguridad
        assertEquals(5L, resultado.getProductoId());
    }
}
