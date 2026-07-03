package hospital.ms_user.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.dto.UserResponseDTO;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapeo de entrada: UserCreateDTO -> User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "roles", ignore = true)
    // El campo username y email han sido eliminados de la entidad, 
    @Mapping(target = "autoEmail", ignore = true) // Se asigna manualmente en el Service
    User toEntity(UserCreateDTO createDTO);

    // Mapeo de salida: User (Entidad) -> UserResponseDTO
    @Mapping(target = "role", expression = "java(mapRolesToString(user.getRoles()))")

    UserResponseDTO toResponseDTO(User user);

    /**
     * obtener la lista completa en Postman sin mostrar contraseñas.
     */
    List<UserResponseDTO> toResponseDTOs(List<User> users);

    default String mapRolesToString(Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            return "ROLE_USER"; 
        }
        return roles.iterator().next().getName();
    }
}