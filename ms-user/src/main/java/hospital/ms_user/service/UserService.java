package hospital.ms_user.service;

import hospital.ms_user.dto.UserDto;
import hospital.ms_user.dto.UserResponseDto;
import hospital.ms_user.model.User;

import java.util.List;

public interface UserService {
    UserResponseDto registrarUsuario(UserDto userDto);
    List<UserResponseDto> obtenerTodos();

    void deleteByEmail(String email);
    java.util.Optional<User> findByEmail(String email);
}