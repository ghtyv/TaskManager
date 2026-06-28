package com.taskmanager.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column (name = "password", nullable = false, length = 255)
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
