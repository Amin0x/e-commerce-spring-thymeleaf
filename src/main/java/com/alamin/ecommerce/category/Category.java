package com.alamin.ecommerce.category;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.time.LocalDateTime;
import lombok.*;

import com.alamin.ecommerce.product.Product;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private String name;
    private String description;
    private String  imageUrl;
    private Boolean active;
    private LocalDateTime created;
    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "categoryId")
    private Category parent;


    // Default constructor (required by JPA)
    public Category() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    // Constructor for convenience
    public Category(String name, String description, Category parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.active = true;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

}
