package com.hospital.pacientes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.pacientes.dto.request.PacienteRequestDTO;
import com.hospital.pacientes.dto.response.PacienteResponseDTO;
import com.hospital.pacientes.exception.DuplicadoException;
import com.hospital.pacientes.exception.ResourceNotFoundException;
import com.hospital.pacientes.mapper.PacienteMapper;
import com.hospital.pacientes.model.Model_pacientes;
import com.hospital.pacientes.repository.Repository_pacientes;

@ExtendWith(MockitoExtension.class)
class Service_pacientesTest {

    @Mock
    private Repository_pacientes repository;

    private PacienteMapper mapper;
    private Service_pacientes service;

    @BeforeEach
    void setUp() {
        mapper = new PacienteMapper();
        service = new Service_pacientes(repository, mapper);
    }

    @Test
    @DisplayName("obtenerTodos retorna lista mapeada cuando existen pacientes")
    void obtenerTodos_conPacientesExistentes_retornaListaMapeada() {
        // Given
        Model_pacientes paciente1 = crearPaciente(1L, "12345678-9", "FONASA");
        Model_pacientes paciente2 = crearPaciente(2L, "98765432-1", "ISAPRE");
        when(repository.findAll()).thenReturn(List.of(paciente1, paciente2));

        // When
        List<PacienteResponseDTO> resultado = service.obtenerTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getRut()).isEqualTo("12345678-9");
        assertThat(resultado.get(1).getPrevision()).isEqualTo("ISAPRE");
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerTodos retorna lista vacía cuando no hay pacientes")
    void obtenerTodos_sinPacientes_retornaListaVacia() {
        // Given
        when(repository.findAll()).thenReturn(List.of());

        // When
        List<PacienteResponseDTO> resultado = service.obtenerTodos();

        // Then
        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId retorna paciente cuando ID existe")
    void obtenerPorId_idExistente_retornaPaciente() {
        // Given
        Model_pacientes paciente = crearPaciente(1L, "12345678-9", "FONASA");
        when(repository.findById(1L)).thenReturn(Optional.of(paciente));

        // When
        PacienteResponseDTO resultado = service.obtenerPorId(1L);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId lanza excepción cuando ID no existe")
    void obtenerPorId_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Paciente no encontrado con ID 99");

        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("guardar crea paciente cuando RUT no existe")
    void guardar_rutNoExistente_guardaCorrectamente() {
        // Given
        PacienteRequestDTO dto = crearRequestDTO("12345678-9", "FONASA");
        Model_pacientes guardado = crearPaciente(1L, "12345678-9", "FONASA");

        when(repository.findByRut("12345678-9")).thenReturn(Optional.empty());
        when(repository.save(any(Model_pacientes.class))).thenReturn(guardado);

        // When
        PacienteResponseDTO resultado = service.guardar(dto);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        verify(repository, times(1)).findByRut("12345678-9");
        verify(repository, times(1)).save(any(Model_pacientes.class));
    }

    @Test
    @DisplayName("guardar lanza excepción cuando RUT ya existe")
    void guardar_rutDuplicado_lanzaExcepcion() {
        // Given
        PacienteRequestDTO dto = crearRequestDTO("12345678-9", "FONASA");
        Model_pacientes existente = crearPaciente(1L, "12345678-9", "FONASA");
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(existente));

        // When / Then
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(DuplicadoException.class)
                .hasMessageContaining("Ya existe un paciente con el RUT");

        verify(repository, times(1)).findByRut("12345678-9");
        verify(repository, never()).save(any(Model_pacientes.class));
    }

    @Test
    @DisplayName("obtenerPorRut retorna paciente cuando RUT existe")
    void obtenerPorRut_rutExistente_retornaPaciente() {
        // Given
        Model_pacientes paciente = crearPaciente(1L, "12345678-9", "FONASA");
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(paciente));

        // When
        PacienteResponseDTO resultado = service.obtenerPorRut("12345678-9");

        // Then
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        verify(repository, times(1)).findByRut("12345678-9");
    }

    @Test
    @DisplayName("obtenerPorRut lanza excepción cuando RUT no existe")
    void obtenerPorRut_rutInexistente_lanzaExcepcion() {
        // Given
        when(repository.findByRut("00000000-0")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorRut("00000000-0"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Paciente no encontrado con RUT 00000000-0");

        verify(repository, times(1)).findByRut("00000000-0");
    }

    @Test
    @DisplayName("obtenerPorPrevision retorna lista cuando existen pacientes")
    void obtenerPorPrevision_existenPacientes_retornaLista() {
        // Given
        Model_pacientes paciente = crearPaciente(1L, "12345678-9", "FONASA");
        when(repository.findByPrevision("FONASA")).thenReturn(List.of(paciente));

        // When
        List<PacienteResponseDTO> resultado = service.obtenerPorPrevision("FONASA");

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPrevision()).isEqualTo("FONASA");
        verify(repository, times(1)).findByPrevision("FONASA");
    }

    @Test
    @DisplayName("obtenerPorPrevision lanza excepción cuando no hay coincidencias")
    void obtenerPorPrevision_sinCoincidencias_lanzaExcepcion() {
        // Given
        when(repository.findByPrevision("DIPRECA")).thenReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> service.obtenerPorPrevision("DIPRECA"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontraron pacientes con la previsión");

        verify(repository, times(1)).findByPrevision("DIPRECA");
    }

    @Test
    @DisplayName("actualizar modifica paciente cuando datos son válidos")
    void actualizar_datosValidos_actualizaCorrectamente() {
        // Given
        Model_pacientes existente = crearPaciente(1L, "12345678-9", "FONASA");
        PacienteRequestDTO dto = crearRequestDTO("12345678-9", "ISAPRE");
        dto.setNombre("Carlos");
        dto.setApellido("Rojas");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        // When
        PacienteResponseDTO resultado = service.actualizar(1L, dto);

        // Then
        assertThat(resultado.getNombre()).isEqualTo("Carlos");
        assertThat(resultado.getPrevision()).isEqualTo("ISAPRE");
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existente);
    }

    @Test
    @DisplayName("actualizar lanza excepción cuando ID no existe")
    void actualizar_idInexistente_lanzaExcepcion() {
        // Given
        PacienteRequestDTO dto = crearRequestDTO("12345678-9", "FONASA");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.actualizar(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Paciente no encontrado con ID 99");

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(Model_pacientes.class));
    }

    @Test
    @DisplayName("actualizar lanza excepción cuando RUT pertenece a otro paciente")
    void actualizar_rutEnUsoPorOtroPaciente_lanzaExcepcion() {
        // Given
        Model_pacientes pacienteActual = crearPaciente(1L, "12345678-9", "FONASA");
        Model_pacientes otroPaciente = crearPaciente(2L, "98765432-1", "ISAPRE");
        PacienteRequestDTO dto = crearRequestDTO("98765432-1", "FONASA");

        when(repository.findById(1L)).thenReturn(Optional.of(pacienteActual));
        when(repository.findByRut("98765432-1")).thenReturn(Optional.of(otroPaciente));

        // When / Then
        assertThatThrownBy(() -> service.actualizar(1L, dto))
                .isInstanceOf(DuplicadoException.class)
                .hasMessageContaining("El RUT ya está en uso por otro paciente");

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).findByRut("98765432-1");
        verify(repository, never()).save(any(Model_pacientes.class));
    }

    @Test
    @DisplayName("actualizar permite mantener el mismo RUT del paciente")
    void actualizar_mismoRutPropio_noLanzaExcepcion() {
        // Given
        Model_pacientes existente = crearPaciente(1L, "12345678-9", "FONASA");
        PacienteRequestDTO dto = crearRequestDTO("12345678-9", "ISAPRE");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        // When
        PacienteResponseDTO resultado = service.actualizar(1L, dto);

        // Then
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
        assertThat(resultado.getPrevision()).isEqualTo("ISAPRE");
        verify(repository, times(1)).save(existente);
    }

    @Test
    @DisplayName("eliminar borra paciente cuando ID existe")
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
    @DisplayName("eliminar lanza excepción cuando ID no existe")
    void eliminar_idInexistente_lanzaExcepcion() {
        // Given
        when(repository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se puede eliminar, ID no encontrado: 99");

        verify(repository, times(1)).existsById(99L);
        verify(repository, never()).deleteById(99L);
    }

    private Model_pacientes crearPaciente(Long id, String rut, String prevision) {
        Model_pacientes paciente = new Model_pacientes();
        paciente.setId(id);
        paciente.setRut(rut);
        paciente.setNombre("Andrea");
        paciente.setApellido("Soto");
        paciente.setEdad(34);
        paciente.setTelefono("+56987654321");
        paciente.setPrevision(prevision);
        return paciente;
    }

    private PacienteRequestDTO crearRequestDTO(String rut, String prevision) {
        PacienteRequestDTO dto = new PacienteRequestDTO();
        dto.setRut(rut);
        dto.setNombre("Andrea");
        dto.setApellido("Soto");
        dto.setEdad(34);
        dto.setTelefono("+56987654321");
        dto.setPrevision(prevision);
        return dto;
    }
}
