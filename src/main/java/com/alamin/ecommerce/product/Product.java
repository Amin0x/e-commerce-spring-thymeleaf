package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "tbl_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
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
    private String image;
    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deleted;

    @OneToOne()
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId", unique = false)
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product",cascade = CascadeType.ALL)
    private List<ProductImage> productImages = new ArrayList<>();

    // Default constructor
    public Product() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        this.active = true;
        this.enabled = true;
        this.viewCount = 0;
        this.stock = 0;
        this.price = 0;
        this.priceUSD = 0;
        this.initPrice = 0;
        this.sku = "SKU" + System.currentTimeMillis();
    }

    // Constructor for convenience
    public Product(
            String name,
            String description,
            Integer price,
            Category category,
            String image ) {
        
        this.name = name;
        this.description = description;
        this.price = price;
        this.initPrice = price;
        this.image = image;
        this.stock = 0;
        this.totalSold = 0;
        this.category = category;
        this.active = false;
        this.enabled = false;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
}
