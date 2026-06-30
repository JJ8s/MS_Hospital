package hospital.ms_user.mapper;

import org.springframework.stereotype.Component;

import hospital.ms_user.dto.UsuarioResponseDTO;
import hospital.ms_user.model.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        String rol = "SIN_ROL";

        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            rol = usuario.getRoles().iterator().next().getName();
        }

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getAutoEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTelefono(),
                rol,
                usuario.isActive()
        );
    }
}