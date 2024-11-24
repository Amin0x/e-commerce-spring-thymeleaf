package com.alamin.ecommerce.invoice;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private Date invoiceDate;
    private double totalAmount;

    // Getters and Setters
}
