package com.taskmanager.task_manager_rest_api_spring_boot.controller;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Users;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void login() {}

    @DeleteMapping("/logout")
    public void logout() {}
}
