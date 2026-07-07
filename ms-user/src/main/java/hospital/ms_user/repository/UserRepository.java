package hospital.ms_user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; 
import hospital.ms_user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    

    Optional<User> findByAutoEmail(String autoEmail);

    

    boolean existsByAutoEmail(String autoEmail);

    

    boolean existsByTelefono(String telefono);

    

    @Transactional 
    void deleteByAutoEmail(String autoEmail);
}