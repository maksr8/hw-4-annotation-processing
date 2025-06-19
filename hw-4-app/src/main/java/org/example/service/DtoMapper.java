package org.example.service;

import org.example.domain.User;
import org.example.dto.UserDto;

public class DtoMapper {
    public User map(UserDto dto) {
//        UserDtoValidator.validate(dto);

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}
