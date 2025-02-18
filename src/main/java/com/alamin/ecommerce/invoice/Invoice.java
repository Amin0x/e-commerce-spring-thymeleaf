package com.alamin.ecommerce.invoice;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Entity
@Table(name = "tbl_invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;
    private String customerName;
    private double totalAmount;

    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    // Getters and Setters
}
