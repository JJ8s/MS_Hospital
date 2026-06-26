package hospital.ms_user.mapper;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.dto.UserResponseDTO;
import hospital.ms_user.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-26T12:32:34-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( createDTO.getUsername() );
        user.setPassword( createDTO.getPassword() );
        user.setUsername( createDTO.getUsername() );

        user.setActive( true );

        return user;
    }

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO.UserResponseDTOBuilder userResponseDTO = UserResponseDTO.builder();

        userResponseDTO.active( user.isActive() );
        userResponseDTO.id( user.getId() );
        userResponseDTO.username( user.getUsername() );

        userResponseDTO.role( mapRolesToString(user.getRoles()) );

        return userResponseDTO.build();
    }
}
