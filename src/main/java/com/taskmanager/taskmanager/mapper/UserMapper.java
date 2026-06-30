package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.UserDto;
import com.taskmanager.taskmanager.dto.UserListItemDto;
import com.taskmanager.taskmanager.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        return new UserDto(email, password);
    }

    public UserListItemDto toListItemDto(User user) {
        Long id = user.getId();
        String email = user.getEmail();

        return new UserListItemDto(id, email);
    }

    public List<UserListItemDto> listToListItemDto(List<User> users) {
        return users
                .stream()
                .map(this::toListItemDto)
                .toList();
    }

}
