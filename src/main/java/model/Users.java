package model;

import jakarta.persistence.*;

@Entity
@Table (name = "users")
public class Users {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    @Column (name = "email", nullable = false)
    private String email;

    @Column (name = "password", nullable = false)
    private String password;

}
