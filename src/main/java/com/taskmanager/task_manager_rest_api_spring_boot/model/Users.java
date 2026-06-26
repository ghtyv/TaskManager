package com.taskmanager.task_manager_rest_api_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table (name = "users")
public class Users {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    @Column (name = "email", nullable = false)
    private String email;

    @JsonIgnore
    @Column (name = "password", nullable = false)
    private String password;

    public String getEmail() {

        return email;

    }

    public String getPassword() {

        return password;

    }

    public void setEmail(String email) {

        this.email = email;

    }

    public void setPassword(String password) {

        this.password = password;

    }
}
