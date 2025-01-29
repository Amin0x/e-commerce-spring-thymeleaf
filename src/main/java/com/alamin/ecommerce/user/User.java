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
    private Long userId;
    @Column(unique = true)
    private String uuid;
    @Column(length = 20)
    private String role;
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private String avatar;
    private Boolean enabled;
    @Column(length = 20)
    private String status;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    private LocalDateTime lastActive;
    private LocalDate birthDate;
    private LocalDateTime created;
    private LocalDateTime updated;

    public User() {
        this.role = "USER";
        this.status = "active";
        this.uuid = new UUID(System.currentTimeMillis(), System.currentTimeMillis()).toString();
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = "USER";
        this.status = "active";
        this.uuid = new UUID(System.currentTimeMillis(), System.currentTimeMillis()).toString();
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    public User(String username, String password, String email, String role, String avatar, String status, Boolean enabled, String firstName, String lastName, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.avatar = avatar;
        this.status = status;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.uuid = new UUID(System.currentTimeMillis(), System.currentTimeMillis()).toString();
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
}
