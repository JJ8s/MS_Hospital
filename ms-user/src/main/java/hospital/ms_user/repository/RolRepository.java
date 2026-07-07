package hospital.ms_user.repository;

import hospital.ms_user.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método para buscar un rol por su nombre (ADMIN, MEDICO, PACIENTE)
    Optional<Rol> findByName(String name);
}