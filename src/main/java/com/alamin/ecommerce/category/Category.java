package com.alamin.ecommerce.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.*;

import com.alamin.ecommerce.product.Product;

@Data
@Entity
@Table(name = "tbl_categories")
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

    @JsonIgnore
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
        if (Objects.equals(this, parent))
            throw new IllegalArgumentException("parent is same as category");
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.active = true;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

}
