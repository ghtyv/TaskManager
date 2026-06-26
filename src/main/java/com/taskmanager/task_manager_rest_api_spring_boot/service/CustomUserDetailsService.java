package com.taskmanager.task_manager_rest_api_spring_boot.service;

import com.taskmanager.task_manager_rest_api_spring_boot.model.CustomUserDetails;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.UsersRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = usersRepository.findUserByEmail(email);

        return new CustomUserDetails(user);

    }
}
