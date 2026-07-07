package com.hospital.ms_medicos.service;
import com.hospital.ms_medicos.repository.Repository_medicos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.hospital.ms_medicos.exception.DuplicadoException;
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
        return repository_medicos.findById(id).orElseThrow(()-> new ResourceNotFoundException("Medico no encontrado con ID" + id));
    }

    public Model_medicos guardar(Model_medicos medicos){
        if(repository_medicos.findByRut(medicos.getRut()).isPresent()){
            throw new DuplicadoException("Ya existe un medico con este RUT" +medicos.getRut());
        }
        return repository_medicos.save(medicos);
    }

    public Model_medicos obtenerPorRut(String rut){
        return repository_medicos.findByRut(rut).orElseThrow(()-> new ResourceNotFoundException("Medico no encontrado con Rut" + rut));        

    }

    public List<Model_medicos> obtenerPorEspecialidad(String especialidad){
        List<Model_medicos> medicos=repository_medicos.findByEspecialidad(especialidad);
        if(medicos.isEmpty()){
            throw new ResourceNotFoundException("No se encontraron medicos con la especialidad" + especialidad);
        }
        return medicos;
    }

    public Model_medicos actualizar(Long id, Model_medicos nuevo){
        Model_medicos medicos = repository_medicos.findById(id).orElseThrow(()-> new ResourceNotFoundException("Medico no encontrado con ID" + id));
        Optional<Model_medicos> existente= repository_medicos.findByRut(nuevo.getRut());
        if(existente.isPresent() && ! existente.get().getId().equals(id)){
            throw new DuplicadoException("El Rut ya esta en uso por otro medico");
        }
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
        }
    public void eliminar(Long id){
        if(!repository_medicos.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar, ID no encontrado: " + id);

        }    
        repository_medicos.deleteById(id);
    }

}
