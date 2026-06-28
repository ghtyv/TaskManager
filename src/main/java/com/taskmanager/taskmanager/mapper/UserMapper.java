package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.UserDto;
import com.taskmanager.taskmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        return new UserDto(email, password);
    }

}
