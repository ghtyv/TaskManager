package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.UserDto;
import com.taskmanager.taskmanager.mapper.UserMapper;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        String email = null;
        String password = null;

        if (userDto.getEmail() != null) {
            email = userDto.getEmail()
                    .describeConstable().orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Provided Email " + userDto.getEmail() + " Is Not Valid")
                    );
        }

        if (userDto.getPassword() != null) {
            password = passwordEncoder.encode(userDto.getPassword())
                    .describeConstable().orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Provided Password " + userDto.getPassword() + " Is Not Valid")
                    );
        }

        var user = new User(email, password);
        var savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

}
