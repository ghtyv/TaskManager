package com.taskmanager.task_manager_rest_api_spring_boot.service;

import com.taskmanager.task_manager_rest_api_spring_boot.model.CustomUserDetails;
import com.taskmanager.task_manager_rest_api_spring_boot.model.Users;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Users> optionalUser = usersRepository.findUserByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }

        Users user = optionalUser.get();

        return new CustomUserDetails(user.getPassword(), user.getEmail());

    }
}
