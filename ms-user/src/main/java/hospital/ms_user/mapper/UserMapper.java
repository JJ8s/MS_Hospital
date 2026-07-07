package hospital.ms_user.mapper;

import hospital.ms_user.dto.UserDto;
import hospital.ms_user.dto.UserResponseDto;
import hospital.ms_user.model.Rol;
import hospital.ms_user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Convierte el Request de Postman a la Entidad (Ignoramos campos que procesará el Service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "autoEmail", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserDto dto);

    // Convierte la Entidad a la Respuesta Segura (Mapea los roles a un Set de Strings)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserResponseDto toResponseDto(User user);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Rol> roles) {
        if (roles == null) return null;
        return roles.stream().map(Rol::getName).collect(Collectors.toSet());
    }
}