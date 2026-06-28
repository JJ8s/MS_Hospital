package hospital.ms_user.mapper;

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

    // Mapeo de entrada (DTO -> Entidad)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "email", source = "username") 
    @Mapping(target = "roles", ignore = true) 
    User toEntity(UserCreateDTO createDTO);

    // Mapeo de salida (Entidad -> DTO de Respuesta)
    @Mapping(target = "fullName", ignore = true) // Opcional: si no está en la entidad
    @Mapping(target = "role", expression = "java(mapRolesToString(user.getRoles()))")
    UserResponseDTO toResponseDTO(User user);

    // Método de apoyo para convertir el Set de Roles en un String
    default String mapRolesToString(Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            return "ROLE_USER"; // Rol por defecto
        }
        // Retorna el nombre del primer rol encontrado
        return roles.iterator().next().getName();
    }
}