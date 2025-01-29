package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "tbl_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer initPrice;
    private Integer priceUSD;
    private String sku;
    private Integer stock;
    private Integer totalSold;
    private Integer viewCount;
    private Boolean active;
    private Boolean enabled;
    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private Set<ProductImage> productImage = new HashSet<>();

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
