package com.alamin.ecommerce.stock;

import jakarta.persistence.*;
import lombok.*;
import com.alamin.ecommerce.product.Product;

import java.time.LocalDateTime;

@Data
@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private int price;
    private LocalDateTime created;
    private LocalDateTime updated;

    @OneToOne
    @JoinColumn(name ="product_id", referencedColumnName = "product_id")
    private Product product;
}
