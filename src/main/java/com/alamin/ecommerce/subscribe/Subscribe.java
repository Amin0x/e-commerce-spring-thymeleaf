package com.alamin.ecommerce.subscribe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity(name = "tbl_subscribe")
public class Subscribe {
    @Id
    private String id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 20)
    private String status;
    private String emailVerificationToken;
    private LocalDateTime emailVerificationTokenExpiration;
    private LocalDateTime lastEmailDate;
}
