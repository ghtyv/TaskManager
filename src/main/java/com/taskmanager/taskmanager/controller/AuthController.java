package com.taskmanager.taskmanager.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName();
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void login() {}

    @DeleteMapping("/logout")
    public void logout() {}
}
