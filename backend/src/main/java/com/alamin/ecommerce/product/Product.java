package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "tbl_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(unique = true, nullable = false)
    private String barcode;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Brand brand;

    private String model;

    private String color;

    private String size;

    private String material;

    private String style;

    @Column(nullable = false)
    private String name;

    private String tags;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    private Integer basePrice;

    private Integer priceUSD;

    @Column(nullable = false)
    private String sku;

    private Integer stock;

    @Column(nullable = false)
    private Integer totalSold;

    @Column(nullable = false)
    private Integer viewCount;

    private Boolean active;

    private Boolean enabled;

    private String image;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime updated;

    private LocalDateTime deleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductPrice> productPrices = new ArrayList<>();

    // Default constructor
    public Product() {
    }

    // Constructor for convenience
    public Product(
            String uuid,
            String slug,
            String barcode,
            String brand,
            String model,
            String name,
            String description,
            Integer price,
            Category category,
            String image ) {
        
        this.name = name;
        this.description = description;
        this.price = price;
        this.basePrice = price;
        this.image = image;
        this.stock = 0;
        this.totalSold = 0;
        this.category = category;
        this.active = false;
        this.enabled = false;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    public void addImage(ProductImage productImage){
        productImages.add(productImage);
        productImage.setProduct(this);
    }

    public void removeImage(ProductImage productImage){
        this.productImages.remove(productImage);
        productImage.setProduct(null);
    }
}
