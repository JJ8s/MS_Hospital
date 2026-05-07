package com.hospital.ms_urgencia.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.ms_urgencia.client.PacienteCliente;
import com.hospital.ms_urgencia.exception.ResourceNotFoundExceptione;
import com.hospital.ms_urgencia.model.Model_urgencias;
import com.hospital.ms_urgencia.repository.Repository_urgencias;

@Service
public class Service_urgencia {
    @Autowired
    private Repository_urgencias repository_urgencias;

    @Autowired
    private PacienteCliente pacienteCliente;

    public List<Model_urgencias> obtenerTodasLasUrgencias() {
        return repository_urgencias.findAll();
    }

    public Model_urgencias guardar(Model_urgencias urgencias){
        /*fixeado para evitar errores al probar inicializar datos
        try{
            pacienteCliente.obtenerPacientePorId(urgencias.getPacienteId());
        }catch(Exception e){
            throw new ResourceNotFoundExceptione("Error: Paciente con id " + urgencias.getPacienteId() + " no encontrado");
        }*/
        return repository_urgencias.save(urgencias);
    }
    public Model_urgencias actualizarTriage(Long id, String nuevoNivel){
        Model_urgencias u = repository_urgencias.findById(id).orElseThrow(()-> new ResourceNotFoundExceptione("Registro de urgencias no encontrado"));
        u.setNivelTriage(nuevoNivel);
        return repository_urgencias.save(u);    
    }
    
    public void eliminar(Long id){
        if(!repository_urgencias.existsById(id)){
            throw new ResourceNotFoundExceptione("No se puede eliminar: Id no existe" + id);

        }
        repository_urgencias.deleteById(id);
    }
}
