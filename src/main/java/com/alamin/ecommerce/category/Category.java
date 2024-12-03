package com.alamin.ecommerce.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.time.LocalDateTime;
import lombok.*;

import com.alamin.ecommerce.product.Product;

@Data
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the id value
    private Long id;

    @NotNull(message = "Category name is required")
    @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    private LocalDateTime createdDate;

    // One-to-many relationship: A category can have many products
    //@OneToMany(mappedBy = "category")
    //private List<Product> products;

    // Default constructor (required by JPA)
    public Category() {}

    // Constructor for convenience
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdDate = LocalDateTime.now();
    }

}
