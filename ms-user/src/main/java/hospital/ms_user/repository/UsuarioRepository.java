package hospital.ms_user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hospital.ms_user.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByUsername(String username);

    // Esto le dice a Spring: "SELECT COUNT(*) > 0 FROM users WHERE username = ?"
    boolean existsByUsername(String username);

    // Esto le dice a Spring: "SELECT COUNT(*) > 0 FROM users WHERE email = ?"
    boolean existsByEmail(String email);

}
