package com.hospital.ms_citas.service;
import com.hospital.ms_citas.client.MedicoCliente;
import com.hospital.ms_citas.client.PacienteCliente;
import com.hospital.ms_citas.exception.HorarioNoDisponibleException;
import com.hospital.ms_citas.exception.ResourceNotFoundExceptione;
import com.hospital.ms_citas.model.EstadoCita;
import com.hospital.ms_citas.model.Model_citas;
import com.hospital.ms_citas.repository.Repository_citas;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class Service_citas {
    
    @Autowired
    private Repository_citas repository_citas;

    @Autowired
    private MedicoCliente medicoCliente;

    @Autowired
    private PacienteCliente pacienteCliente;

    public List<Model_citas> listarTodas(){
        return repository_citas.findAll();
    }
    public Optional<Model_citas> buscarPorId(Long id){
        return repository_citas.findById(id);
    }   

    public Model_citas agendarCita(Model_citas cita){
        try{
            medicoCliente.obtenerMedicoPorId(cita.getMedicoId());
        }catch(FeignException.NotFound  e){
            throw new ResourceNotFoundExceptione("Medico con ID" + cita.getMedicoId() + "no existe");
        }
        try{
            pacienteCliente.obtenerPacientePorId(cita.getPacienteId());
        }catch(FeignException.NotFound e){
            throw new ResourceNotFoundExceptione("Paciente con ID" + cita.getPacienteId() + "no existe");

        } 
        List<Model_citas> conflictos = repository_citas
                .findByMedicoIdAndFechaAndHoraAndEstadoNot(
                        cita.getMedicoId(),
                        cita.getFecha(),
                        cita.getHora(),
                        EstadoCita.CANCELADA);
        if(!conflictos.isEmpty()){
            throw new HorarioNoDisponibleException("El medico ya tiene una cita agendada en ese horario");
        }
        cita.setEstado(EstadoCita.PENDIENTE);
        return repository_citas.save(cita);
    }      
    public void cancelarCita(Long id) {
        Model_citas cita = repository_citas.findById(id).orElseThrow(() -> new ResourceNotFoundExceptione("No se puede cancelar: Cita con ID " + id + " no encontrada"));
        cita.setEstado(EstadoCita.CANCELADA);
        repository_citas.save(cita);
    }   
    public void eliminarFisicamente(Long id) {
    if(!repository_citas.existsById(id)){
        throw new ResourceNotFoundExceptione("Nose puede eliminar, ID no encontrado " + id);
            }
        repository_citas.deleteById(id);
        } 

    }
