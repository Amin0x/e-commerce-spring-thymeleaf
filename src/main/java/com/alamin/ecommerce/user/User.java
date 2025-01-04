package com.alamin.ecommerce.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long userId;
    @Column(length = 20)
    private String role;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private Boolean enabled;
    @Column(length = 20)
    private String status;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    private LocalDate birthDate;
    private LocalDateTime created;
    private LocalDateTime updated;

}
