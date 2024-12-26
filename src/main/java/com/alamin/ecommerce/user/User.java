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
    private String role;
    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private LocalDate birthDate;
    private LocalDateTime created;
    private LocalDateTime updated;

}
