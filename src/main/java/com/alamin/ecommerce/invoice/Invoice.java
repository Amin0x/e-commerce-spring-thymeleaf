package com.alamin.ecommerce.invoice;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;

@Data
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private double totalAmount;

    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    // Getters and Setters
}
