package com.hospital.ms_medicos.service;

import com.hospital.ms_medicos.dto.request.MedicoRequestDTO;
import com.hospital.ms_medicos.dto.response.MedicoResponseDTO;
import com.hospital.ms_medicos.exception.DuplicadoException;
import com.hospital.ms_medicos.exception.ResourceNotFoundException;
import com.hospital.ms_medicos.mapper.MedicoMapper;
import com.hospital.ms_medicos.model.Model_medicos;
import com.hospital.ms_medicos.repository.Repository_medicos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Service_medicosTest {

    @Mock
    private Repository_medicos repository;

    // Se usa la instancia real del mapper porque es lógica de mapeo pura,
    // no una dependencia externa que deba mockearse.
    private MedicoMapper mapper;

    private Service_medicos service;

    @BeforeEach
    void setUp() {
        mapper = new MedicoMapper();
        service = new Service_medicos(repository, mapper);
    }

    // ----------------------------------------------------------------
    // Helpers de construcción de datos de prueba
    // ----------------------------------------------------------------

    private Model_medicos crearMedico(Long id, String rut, String especialidad) {
        Model_medicos medico = new Model_medicos();
        medico.setId(id);
        medico.setRut(rut);
        medico.setNombre("Juan");
        medico.setApellido("Pérez");
        medico.setEspecialidad(especialidad);
        medico.setSector("Urgencias");
        medico.setEmail("juan.perez@hospital.com");
        medico.setTelefono("+56912345678");
        medico.setSueldo(1500000.0);
        medico.setFecha_contratacion(LocalDate.of(2022, 3, 1));
        medico.setAnnios_edad(35);
        medico.setAnnios_experiencia(8);
        return medico;
    }

    private MedicoRequestDTO crearRequestDTO(String rut, String especialidad) {
        MedicoRequestDTO dto = new MedicoRequestDTO();
        dto.setRut(rut);
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setEspecialidad(especialidad);
        dto.setSector("Urgencias");
        dto.setEmail("juan.perez@hospital.com");
        dto.setTelefono("+56912345678");
        dto.setSueldo(1500000.0);
        dto.setFecha_contratacion(LocalDate.of(2022, 3, 1));
        dto.setAnnios_edad(35);
        dto.setAnnios_experiencia(8);
        return dto;
    }

    // ----------------------------------------------------------------
    // obtenerTodos()
    // ----------------------------------------------------------------

    @Test
    @DisplayName("obtenerTodos: debe retornar la lista mapeada cuando existen médicos")
    void obtenerTodos_conMedicosExistentes_retornaListaMapeada() {
        // Given
        Model_medicos medico1 = crearMedico(1L, "11111111-1", "Cardiología");
        Model_medicos medico2 = crearMedico(2L, "22222222-2", "Pediatría");
        when(repository.findAll()).thenReturn(List.of(medico1, medico2));

        // When
        List<MedicoResponseDTO> resultado = service.obtenerTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getRut()).isEqualTo("11111111-1");
        assertThat(resultado.get(1).getEspecialidad()).isEqualTo("Pediatría");
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodos: debe retornar lista vacía cuando no hay médicos")
    void obtenerTodos_sinMedicos_retornaListaVacia() {
        // Given
        when(repository.findAll()).thenReturn(List.of());

        // When
        List<MedicoResponseDTO> resultado = service.obtenerTodos();

        // Then
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAll();
    }

    // ----------------------------------------------------------------
    // obtenerPorId()
    // ----------------------------------------------------------------

    @Test
    @DisplayName("obtenerPorId: debe retornar el médico cuando el ID existe")
    void obtenerPorId_idExistente_retornaMedico() {
        // Given
        Model_medicos medico = crearMedico(1L, "11111111-1", "Cardiología");
        when(repository.findById(1L)).thenReturn(Optional.of(medico));

        // When
        MedicoResponseDTO resultado = service.obtenerPorId(1L);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("11111111-1");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId: debe lanzar ResourceNotFoundException cuando el ID no existe")
    void obtenerPorId_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, times(1)).findById(99L);
    }

    // ----------------------------------------------------------------
    // guardar()
    // ----------------------------------------------------------------

    @Test
    @DisplayName("guardar: debe persistir y retornar el médico cuando el RUT no existe")
    void guardar_rutNoExistente_guardaCorrectamente() {
        // Given
        MedicoRequestDTO dto = crearRequestDTO("33333333-3", "Neurología");
        Model_medicos guardado = crearMedico(5L, "33333333-3", "Neurología");

        when(repository.findByRut("33333333-3")).thenReturn(Optional.empty());
        when(repository.save(any(Model_medicos.class))).thenReturn(guardado);

        // When
        MedicoResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getRut()).isEqualTo("33333333-3");
        verify(repository, times(1)).findByRut("33333333-3");
        verify(repository, times(1)).save(any(Model_medicos.class));
    }

    @Test
    @DisplayName("guardar: debe lanzar DuplicadoException cuando el RUT ya existe")
    void guardar_rutDuplicado_lanzaExcepcion() {
        // Given
        MedicoRequestDTO dto = crearRequestDTO("11111111-1", "Cardiología");
        Model_medicos existente = crearMedico(1L, "11111111-1", "Cardiología");

        when(repository.findByRut("11111111-1")).thenReturn(Optional.of(existente));

        // When / Then
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(DuplicadoException.class)
                .hasMessageContaining("11111111-1");

        verify(repository, never()).save(any(Model_medicos.class));
    }

    // ----------------------------------------------------------------
    // obtenerPorRut()
    // ----------------------------------------------------------------

    @Test
    @DisplayName("obtenerPorRut: debe retornar el médico cuando el RUT existe")
    void obtenerPorRut_rutExistente_retornaMedico() {
        // Given
        Model_medicos medico = crearMedico(1L, "11111111-1", "Cardiología");
        when(repository.findByRut("11111111-1")).thenReturn(Optional.of(medico));

        // When
        MedicoResponseDTO resultado = service.obtenerPorRut("11111111-1");

        // Then
        assertThat(resultado.getRut()).isEqualTo("11111111-1");
        verify(repository, times(1)).findByRut("11111111-1");
    }

    @Test
    @DisplayName("obtenerPorRut: debe lanzar ResourceNotFoundException cuando el RUT no existe")
    void obtenerPorRut_rutInexistente_lanzaExcepcion() {
        // Given
        when(repository.findByRut("99999999-9")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorRut("99999999-9"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99999999-9");
    }

    // ----------------------------------------------------------------
    // obtenerPorEspecialidad()
    // ----------------------------------------------------------------

    @Test
    @DisplayName("obtenerPorEspecialidad: debe retornar lista cuando existen médicos con esa especialidad")
    void obtenerPorEspecialidad_existenMedicos_retornaLista() {
        // Given
        Model_medicos medico = crearMedico(1L, "11111111-1", "Cardiología");
        when(repository.findByEspecialidad("Cardiología")).thenReturn(List.of(medico));

        // When
        List<MedicoResponseDTO> resultado = service.obtenerPorEspecialidad("Cardiología");

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEspecialidad()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("obtenerPorEspecialidad: debe lanzar ResourceNotFoundException cuando no hay coincidencias")
    void obtenerPorEspecialidad_sinCoincidencias_lanzaExcepcion() {
        // Given
        when(repository.findByEspecialidad("Dermatología")).thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorEspecialidad("Dermatología"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Dermatología");
    }

    // ----------------------------------------------------------------
    // actualizar()
    // ----------------------------------------------------------------

    @Test
    @DisplayName("actualizar: debe actualizar correctamente cuando el ID existe y el RUT no está en uso")
    void actualizar_datosValidos_actualizaCorrectamente() {
        // Given
        Long id = 1L;
        Model_medicos existente = crearMedico(id, "11111111-1", "Cardiología");
        MedicoRequestDTO dto = crearRequestDTO("11111111-1", "Cardiología Infantil");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.findByRut("11111111-1")).thenReturn(Optional.of(existente));
        when(repository.save(any(Model_medicos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MedicoResponseDTO resultado = service.actualizar(id, dto);

        // Then
        assertThat(resultado.getEspecialidad()).isEqualTo("Cardiología Infantil");
        verify(repository, times(1)).save(existente);
    }

    @Test
    @DisplayName("actualizar: debe lanzar ResourceNotFoundException cuando el ID no existe")
    void actualizar_idInexistente_lanzaExcepcion() {
        // Given
        MedicoRequestDTO dto = crearRequestDTO("11111111-1", "Cardiología");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.actualizar(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any(Model_medicos.class));
    }

    @Test
    @DisplayName("actualizar: debe lanzar DuplicadoException cuando el RUT pertenece a otro médico")
    void actualizar_rutEnUsoPorOtroMedico_lanzaExcepcion() {
        // Given
        Long id = 1L;
        Model_medicos existente = crearMedico(id, "11111111-1", "Cardiología");
        Model_medicos otroMedico = crearMedico(2L, "22222222-2", "Pediatría");
        MedicoRequestDTO dto = crearRequestDTO("22222222-2", "Cardiología");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.findByRut("22222222-2")).thenReturn(Optional.of(otroMedico));

        // When / Then
        assertThatThrownBy(() -> service.actualizar(id, dto))
                .isInstanceOf(DuplicadoException.class)
                .hasMessageContaining("RUT");

        verify(repository, never()).save(any(Model_medicos.class));
    }

    @Test
    @DisplayName("actualizar: debe permitir actualizar manteniendo el mismo RUT propio")
    void actualizar_mismoRutPropio_noLanzaExcepcion() {
        // Given:
        Long id = 1L;
        Model_medicos existente = crearMedico(id, "11111111-1", "Cardiología");
        MedicoRequestDTO dto = crearRequestDTO("11111111-1", "Cardiología");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.findByRut("11111111-1")).thenReturn(Optional.of(existente));
        when(repository.save(any(Model_medicos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MedicoResponseDTO resultado = service.actualizar(id, dto);

        // Then
        assertThat(resultado.getRut()).isEqualTo("11111111-1");
        verify(repository, times(1)).save(existente);
    }

    @Test
    @DisplayName("eliminar: debe eliminar el médico cuando el ID existe")
    void eliminar_idExistente_eliminaCorrectamente() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.eliminar(1L);

        // Then
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: debe lanzar ResourceNotFoundException cuando el ID no existe")
    void eliminar_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(anyLong());
    }
}