package com.hospital.ms_citas.service;
import com.hospital.ms_citas.client.MedicoCliente;
import com.hospital.ms_citas.exception.ResourceNotFoundExceptione;
import com.hospital.ms_citas.model.Model_citas;
import com.hospital.ms_citas.repository.Repository_citas;
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


    public List<Model_citas> listarTodas(){
        return repository_citas.findAll();
    }
    public Optional<Model_citas> buscarPorId(Long id){
        return repository_citas.findById(id);
    }   

    public Model_citas agendarCita(Model_citas cita){
        try{
            medicoCliente.obtenerMedicoPorId(cita.getMedicoId());
        }catch(Exception e){
            throw new RuntimeException("Error: el medico con Id"+ cita.getMedicoId() + "no existe o el servicio esta caido");
        }
        cita.setEstado("Pendiente");
        return repository_citas.save(cita);
    }
    public void cancelarCita(Long id) {
        Model_citas cita = repository_citas.findById(id).orElseThrow(() -> new ResourceNotFoundExceptione("No se puede cancelar: Cita con ID " + id + " no encontrada"));
        cita.setEstado("CANCELADA");
        repository_citas.save(cita);
    }
public void eliminarFisicamente(Long id) {
        Model_citas cita = repository_citas.findById(id).orElseThrow(() -> new ResourceNotFoundExceptione("No se puede eliminar: Cita con ID " + id + " no encontrada"));
        repository_citas.delete(cita);
    }

}
