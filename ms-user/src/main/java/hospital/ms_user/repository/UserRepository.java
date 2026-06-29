package hospital.ms_user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hospital.ms_user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su correo institucional generado automáticamente.
     * Este será el método principal que usará ms-auth para la autenticación.
     */
    Optional<User> findByAutoEmail(String autoEmail);

    /**
     * Verifica si el correo institucional ya existe para evitar duplicados 
     * durante la generación automática en el UserService.
     */
    boolean existsByAutoEmail(String autoEmail);

    /**
     * Verifica si el número de teléfono ya está registrado, 
     * dado que es un campo único en tu modelo.
     */
    boolean existsByTelefono(String telefono);
}