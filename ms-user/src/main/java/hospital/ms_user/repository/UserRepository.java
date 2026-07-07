package hospital.ms_user.repository;

import hospital.ms_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Métodos de validación antes de registrar un usuario nuevo
    boolean existsByAutoEmail(String autoEmail);
    boolean existsByTelefono(String telefono);

    // 🌟 NUEVO MÉTODO: Busca al usuario completo usando su correo
    // Crucial para la desactivación (DELETE) y la seguridad interna (auth-data)
    Optional<User> findByAutoEmail(String autoEmail);
}