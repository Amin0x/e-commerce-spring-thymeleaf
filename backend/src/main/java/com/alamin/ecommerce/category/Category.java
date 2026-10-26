package com.alamin.ecommerce.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.*;

@Data
@Entity
@Table(name = "tbl_categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String slug;
    private String name;
    private String description;
    private String  imageUrl;
    private Boolean active;
    private Boolean enabled;
    private LocalDateTime created;
    private LocalDateTime updated;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Category parent;

    public Category() {
    }

    // Constructor for convenience
    public Category(String name, String description, Category parent, String imageUrl, String slug) {
        if (Objects.equals(this, parent))
            throw new IllegalArgumentException("parent is same as category");
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.active = false;
        this.enabled = false;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        this.imageUrl = imageUrl;
        this.slug = slug;
    }


    // Update parent category
    public void updateParent(Category parent) {
        if (Objects.equals(this, parent))
            throw new IllegalArgumentException("parent is same as category");
        this.parent = parent;
    }

}
