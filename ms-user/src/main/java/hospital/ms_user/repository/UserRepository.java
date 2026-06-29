package hospital.ms_user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hospital.ms_user.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);

    //  "SELECT COUNT(*) > 0 FROM users WHERE username = ?"
    boolean existsByUsername(String username);

    // "SELECT COUNT(*) > 0 FROM users WHERE email = ?"
    boolean existsByEmail(String email);

}
