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

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "roles", ignore = true)
    
    @Mapping(target = "autoEmail", ignore = true) 
    User toEntity(UserCreateDTO createDTO);

    
    @Mapping(target = "role", expression = "java(mapRolesToString(user.getRoles()))")

    UserResponseDTO toResponseDTO(User user);

    

    List<UserResponseDTO> toResponseDTOs(List<User> users);

    default String mapRolesToString(Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) {
            return "ROLE_USER"; 
        }
        return roles.iterator().next().getName();
    }
}