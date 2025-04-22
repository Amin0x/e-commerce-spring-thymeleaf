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
    @Column(nullable = false)
    private String verificationToken;
    @Column(nullable = false)
    private LocalDateTime tokenExpiration;
    private LocalDateTime EmailDate;
    private LocalDateTime createdAt;

    public Subscribe() {
    }

    public Subscribe(String email, String name, String status, String verificationToken,
                     LocalDateTime tokenExpiration, LocalDateTime EmailDate, LocalDateTime createdAt) {
        this.email = email;
        this.name = name;
        this.status = status;
        this.verificationToken = verificationToken;
        this.tokenExpiration = tokenExpiration;
        this.EmailDate = EmailDate;
        this.createdAt = createdAt;
    }
}
