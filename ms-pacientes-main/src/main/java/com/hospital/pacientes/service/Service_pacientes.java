package com.hospital.pacientes.service;

import com.hospital.pacientes.repository.Repository_pacientes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.pacientes.exception.ResourceNotFoundExceptione;
import com.hospital.pacientes.model.Model_pacientes;
import java.util.List;


@Service
public class Service_pacientes {
    @Autowired

    private Repository_pacientes repository;

    public List<Model_pacientes> obtenerTodos(){
        return repository.findAll();
    }

    public Model_pacientes guardar(Model_pacientes pacientes){
        return repository.save(pacientes);
    }
    public Model_pacientes obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(()-> new ResourceNotFoundExceptione("Paciente no encontrado con ID"+id));
    }
    public Model_pacientes obtenerPorRut(String rut){
        return repository.findByRut(rut).orElseThrow(()-> new ResourceNotFoundExceptione("Paciente no encontrado con Rut"+rut));
    }
    public Model_pacientes actualizar(long id, Model_pacientes nuevo){
        return repository.findById(id).map(pacientes->{
            pacientes.setRut(nuevo.getRut());
            pacientes.setNombre(nuevo.getNombre());
            pacientes.setApellido(nuevo.getApellido());
            pacientes.setEdad(nuevo.getEdad());
            pacientes.setTelefono(nuevo.getTelefono());
            pacientes.setPrevision(nuevo.getPrevision());
            return repository.save(pacientes);
        }).orElseThrow(()-> new ResourceNotFoundExceptione("No se puede actualizar, id inexistente"+id));
    }
    public void eliminar(Long id){
        Model_pacientes pacientes= repository.findById(id).orElseThrow(()-> new ResourceNotFoundExceptione("No se encontro ninguna id por eliminar con:" + id));
        repository.delete(pacientes);
    }
}
