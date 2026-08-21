package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.UserDto;
import com.taskmanager.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName();
    }

    @PostMapping(path = "/registration", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void registerUser(@Valid @ModelAttribute UserDto userDto) {
        userService.registerUser(userDto);
    }

    @DeleteMapping("/logout")
    public void logout() {}
}
