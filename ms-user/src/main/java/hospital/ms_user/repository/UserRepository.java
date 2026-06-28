package hospital.ms_user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hospital.ms_user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su correo generado automáticamente.
     * Reemplaza a findByUsername. Es CRÍTICO para el "Paso 2" del arranque: 
     * ms-auth (8082) debe actualizarse para usar este campo al validar 
     * credenciales y generar el token JWT [1].
     */
    Optional<User> findByAutoEmail(String autoEmail);

    /**
     * Verifica si ya existe este correo en db_user.
     * Útil para asegurar que no se registren dos miembros del personal 
     * con el mismo identificador generado [2].
     */
    boolean existsByAutoEmail(String autoEmail);
}