package com.alamin.ecommerce.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the id value
    private Long id;

    @NotNull(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @NotNull(message = "Price is required")
    private Integer price;

    private Integer initialPrice;

    @NotNull(message = "Product sku is required")
    @Size(min = 3, max = 100, message = "Product sku must be between 3 and 100 characters")
    private String sku;

    @NotNull(message = "Stock is required")
    private Integer stock;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    // Default constructor (required by JPA)
    public Product() {}

    // Constructor for convenience
    public Product(String name, String description, Integer price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
}
