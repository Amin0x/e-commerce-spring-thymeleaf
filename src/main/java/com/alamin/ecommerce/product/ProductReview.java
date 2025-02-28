package com.alamin.ecommerce.product;

import jakarta.persistence.*;

@Entity
@Table(name="tbl_product_reviews")
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String review;
    private String reviewType;
    private int star;
    private Long userId;
    private Long productId;
}
