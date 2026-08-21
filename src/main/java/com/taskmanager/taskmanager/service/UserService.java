package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.UserDto;
import com.taskmanager.taskmanager.dto.UserListItemDto;
import com.taskmanager.taskmanager.mapper.UserMapper;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public void registerUser(UserDto userDto) {
        var email = userDto.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided Email " + email + " Already Exists"
            );
        }

        var password = passwordEncoder.encode(userDto.getPassword());

        var user = new User(email, password);
        userRepository.save(user);
    }

    @Transactional
    public List<UserListItemDto> findAllUsers() {
        return userMapper.listToListItemDto(userRepository.findAll());
    }

}
