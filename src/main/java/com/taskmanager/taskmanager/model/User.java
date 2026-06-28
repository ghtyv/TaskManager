package com.taskmanager.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table (name = "users")
public class User {

    @Getter
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column (name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Setter
    @Getter
    @JsonIgnore
    @Column (name = "password", nullable = false, length = 255)
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
