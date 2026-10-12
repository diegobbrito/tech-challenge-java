package br.com.techchallenge.techchallenge.controllers.mappers;

import br.com.techchallenge.techchallenge.dtos.UserResponseDTO;
import br.com.techchallenge.techchallenge.entities.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserLogin(),
                user.getAddress(),
                user.getUserType().toString()
        );
    }
}
