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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product",cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product",cascade = CascadeType.ALL)
    private List<ProductPrice> productPrices = new ArrayList<>();

    // Default constructor
    public Product() {
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

    public void addImage(ProductImage productImage){
        productImages.add(productImage);
        productImage.setProduct(this);
    }

    public void removeImage(ProductImage productImage){
        this.productImages.remove(productImage);
        productImage.setProduct(null);
    }
}
