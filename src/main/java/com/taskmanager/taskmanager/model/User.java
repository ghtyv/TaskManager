package com.taskmanager.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "users")
public class User {

    @Getter
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Setter
    @Getter
    @JsonIgnore
    @Column (name = "password", nullable = false)
    private String password;

}
