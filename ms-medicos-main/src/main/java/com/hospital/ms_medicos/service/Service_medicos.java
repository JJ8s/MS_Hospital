package com.hospital.ms_medicos.service;
import com.hospital.ms_medicos.repository.Repository_medicos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.hospital.ms_medicos.exception.ResourceNotFoundException;
import com.hospital.ms_medicos.model.Model_medicos;

@Service
public class Service_medicos {
    @Autowired
    private Repository_medicos repository_medicos;

    public List<Model_medicos> obtenerTodos(){
        return repository_medicos.findAll();
    }
    public Model_medicos obtenerPorId(Long id){
        return repository_medicos.findById(id).orElseThrow(()-> new ResourceNotFoundException("Medico no encontrado con ID"+id));
    }
    public Model_medicos guardar(Model_medicos medicos){
        return repository_medicos.save(medicos);
    }
    public Model_medicos obtenerPorRut(String rut){
        return repository_medicos.findByRut(rut).orElseThrow(()-> new ResourceNotFoundException("Medico no encontrado con Rut"+rut));        
    }
    public List<Model_medicos> obtenerPorEspecialidad(String especialidad){
        List<Model_medicos> medicos=repository_medicos.findByEspecialidad(especialidad);
        if(medicos.isEmpty()){
            throw new ResourceNotFoundException("No se encontraron medicos con la especialidad"+especialidad);
        }
        return medicos;
    }
    public Model_medicos actualizar(Long id, Model_medicos nuevo){
        return repository_medicos.findById(id).map(medicos->{
            medicos.setRut(nuevo.getRut());
            medicos.setNombre(nuevo.getNombre());
            medicos.setApellido(nuevo.getApellido());
            medicos.setEspecialidad(nuevo.getEspecialidad());
            medicos.setSector(nuevo.getSector());
            medicos.setEmail(nuevo.getEmail());
            medicos.setTelefono(nuevo.getTelefono());
            medicos.setSueldo(nuevo.getSueldo());
            medicos.setFecha_contratacion(nuevo.getFecha_contratacion());
            medicos.setAnnios_edad(nuevo.getAnnios_edad());
            medicos.setAnnios_experiencia(nuevo.getAnnios_experiencia());   
            return repository_medicos.save(medicos);
        }).orElseThrow(()-> new ResourceNotFoundException("Medico no encontrado con ID"+id));
    }
    public void eliminar(Long id){
        Model_medicos medicos = repository_medicos.findById(id).orElseThrow(()-> new ResourceNotFoundException("No se puede eliminar, ID no encontrada" + id));
        repository_medicos.delete(medicos);
    }

}
