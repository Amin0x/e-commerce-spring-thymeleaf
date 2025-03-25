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
    private String name;
    private String description;
    private String  imageUrl;
    private Boolean active;
    private Boolean enabled;
    private LocalDateTime created;
    private LocalDateTime updated;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> children = new HashSet<>();

    public Category() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        this.active = true;
        this.enabled = true;
    }

    // Constructor for convenience
    public Category(String name, String description, Category parent) {
        if (Objects.equals(this, parent))
            throw new IllegalArgumentException("parent is same as category");
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.active = true;
        this.enabled = true;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    // Add a child category
    public void addChild(Category category) {
        if (Objects.equals(this, category))
            throw new IllegalArgumentException("category is same as parent");
        children.add(category);
        category.setParent(this);
    }

    // Remove a child category
    public void removeChild(Category category) {
        if(children.remove(category))
            category.setParent(null);
    }

    // Update parent category
    public void updateParent(Category parent) {
        if (Objects.equals(this, parent))
            throw new IllegalArgumentException("parent is same as category");
        this.parent = parent;
        parent.addChild(this);
    }

}
