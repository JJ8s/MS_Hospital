package hospital.ms_user.mapper;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.dto.UserResponseDTO;
import hospital.ms_user.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T05:43:08-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        User user = new User();

        user.setApellido( createDTO.getApellido() );
        user.setContrasena( createDTO.getContrasena() );
        user.setNombre( createDTO.getNombre() );
        user.setTelefono( createDTO.getTelefono() );

        user.setActive( true );

        return user;
    }

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setActive( user.isActive() );
        userResponseDTO.setApellido( user.getApellido() );
        userResponseDTO.setAutoEmail( user.getAutoEmail() );
        userResponseDTO.setId( user.getId() );
        userResponseDTO.setNombre( user.getNombre() );
        userResponseDTO.setTelefono( user.getTelefono() );

        userResponseDTO.setRole( mapRolesToString(user.getRoles()) );

        return userResponseDTO;
    }

    @Override
    public List<UserResponseDTO> toResponseDTOs(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponseDTO> list = new ArrayList<UserResponseDTO>( users.size() );
        for ( User user : users ) {
            list.add( toResponseDTO( user ) );
        }

        return list;
    }
}
