package com.hospital.ms_urgencia.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hospital.ms_urgencia.model.Model_urgencias;

@Repository
public interface Repository_urgencias extends JpaRepository<Model_urgencias, Long>{
    
}
