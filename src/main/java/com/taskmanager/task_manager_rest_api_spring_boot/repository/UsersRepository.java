package com.taskmanager.task_manager_rest_api_spring_boot.repository;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    User findUserByEmail(String email);

}

