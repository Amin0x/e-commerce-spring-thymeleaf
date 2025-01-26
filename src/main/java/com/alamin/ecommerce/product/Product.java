package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    @NotNull(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    @NotNull(message = "Price is required")
    private Integer price;
    private Integer initPrice;
    @NotNull(message = "Product sku is required")
    @Size(min = 3, max = 100, message = "Product sku must be between 3 and 100 characters")
    private String sku;
    @NotNull(message = "Stock is required")
    private Integer stock;
    private int totalSold;
    private int viewCount;
    private Boolean active;
    private Boolean enabled;
    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    @NotNull(message = "Category is required")
    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private Category category;

    // Default constructor (required by JPA)
    public Product() {}

    // Constructor for convenience
    public Product(String name, 
                   String description, 
                   Integer price, 
                   Category category
                   ) {
        
        this.name = name;
        this.description = description;
        this.price = price;
        this.initPrice = price;
        this.stock = 0;
        this.totalSold = 0;
        this.category = category;
        this.active = false;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
}
