package hospital.ms_user.mapper;

import hospital.ms_user.dto.UserCreateDTO;
import hospital.ms_user.dto.UserResponseDTO;
import hospital.ms_user.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-16T10:12:54-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        user.setUsername( createDTO.getUsername() );
        user.setPassword( createDTO.getPassword() );

        user.setActive( true );

        return user;
    }

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO.UserResponseDTOBuilder userResponseDTO = UserResponseDTO.builder();

        userResponseDTO.id( user.getId() );
        userResponseDTO.username( user.getUsername() );
        userResponseDTO.active( user.isActive() );

        userResponseDTO.role( mapRolesToString(user.getRoles()) );

        return userResponseDTO.build();
    }
}
