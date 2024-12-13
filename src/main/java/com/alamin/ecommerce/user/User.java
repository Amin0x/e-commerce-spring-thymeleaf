package com.alamin.ecommerce.user;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class User {
    @Id
    private Long id;
    private String role;
    private String username;
    private String password;
    private Boolean enabled;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Empty class body
}
