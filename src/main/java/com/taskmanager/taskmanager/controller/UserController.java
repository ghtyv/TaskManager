package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.UserDto;
import com.taskmanager.taskmanager.dto.UserListItemDto;
import com.taskmanager.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserListItemDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

}
