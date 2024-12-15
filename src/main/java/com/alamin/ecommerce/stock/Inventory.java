package com.alamin.ecommerce.stock;

import jakarta.persistence.*;
import lombok.*;
import com.alamin.ecommerce.product.Product;

@Data
@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private int price;
    @OneToOne
    private Product product;
    private LocalDateTime created;
    private LocalDateTime updated;
}
