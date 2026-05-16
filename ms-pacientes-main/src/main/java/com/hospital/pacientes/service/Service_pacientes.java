package com.hospital.pacientes.service;

import com.hospital.pacientes.repository.Repository_pacientes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.pacientes.exception.DuplicadoException;
import com.hospital.pacientes.exception.ResourceNotFoundExceptione;
import com.hospital.pacientes.model.Model_pacientes;
import java.util.List;
import java.util.Optional;


@Service
public class Service_pacientes {
    @Autowired

    private Repository_pacientes repository;

    public List<Model_pacientes> obtenerTodos(){
        return repository.findAll();
    }

    public Model_pacientes guardar(Model_pacientes pacientes){
        if(repository.findByRut(pacientes.getRut()).isPresent()){
            throw new DuplicadoException("Ya existe un paciente con el RUT: " + pacientes.getRut());
        }
        return repository.save(pacientes);
    }

    public Model_pacientes obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(()-> new ResourceNotFoundExceptione("Paciente no encontrado con ID"+id));
    }

    public Model_pacientes obtenerPorRut(String rut){
        return repository.findByRut(rut).orElseThrow(()-> new ResourceNotFoundExceptione("Paciente no encontrado con Rut"+rut));
    }

    public Model_pacientes actualizar(long id, Model_pacientes nuevo){
        Model_pacientes pacientes = repository.findById(id).orElseThrow(()-> new ResourceNotFoundExceptione("No se puede actualizar, ID inexistente: " + id));
        Optional<Model_pacientes> existente = repository.findByRut(nuevo.getRut());
        if(existente.isPresent() && !existente.get().getId().equals(id)){
            throw new DuplicadoException("Ya existe un paciente con el RUT: ");
        }
            pacientes.setRut(nuevo.getRut());
            pacientes.setNombre(nuevo.getNombre());
            pacientes.setApellido(nuevo.getApellido());
            pacientes.setEdad(nuevo.getEdad());
            pacientes.setTelefono(nuevo.getTelefono());
            pacientes.setPrevision(nuevo.getPrevision());
            return repository.save(pacientes);
        }
    
    public void eliminar(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundExceptione("No se puede eliminar, ID inexistente: " + id);
        }
        repository.deleteById(id);
    }
}
