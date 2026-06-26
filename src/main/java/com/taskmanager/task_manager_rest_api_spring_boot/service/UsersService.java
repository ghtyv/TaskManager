package com.taskmanager.task_manager_rest_api_spring_boot.service;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.UsersRepository;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users createUser(String email, String password) {

        Users user = new Users();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        usersRepository.save(user);

        return user;

    }

}
