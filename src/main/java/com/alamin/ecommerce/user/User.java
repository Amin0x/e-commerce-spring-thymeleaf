package com.alamin.ecommerce.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tbl_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String uuid;
    @Column(nullable = false, length = 20)
    private String role;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private String avatar;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    private LocalDateTime lastActive;
    private LocalDate birthDate;
    @Column(nullable = false)
    private LocalDateTime created;
    private LocalDateTime updated;
}
