package com.taskmanager.task_manager_rest_api_spring_boot.controller;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Users;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.UsersRepository;
import com.taskmanager.task_manager_rest_api_spring_boot.service.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @PostMapping
    public Users createUser(@RequestBody @NonNull Users user) {

        String email = user.getEmail();
        String password = user.getPassword();

        return usersService.createUser(email, password);

    }

}
