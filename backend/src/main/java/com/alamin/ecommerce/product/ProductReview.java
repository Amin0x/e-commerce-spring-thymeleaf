package com.alamin.ecommerce.product;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="tbl_product_reviews")
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String review;
    private String reviewType;
    private int star;
    private int positiveLike;
    private int negativeLike;
    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getPositiveLike() {
        return positiveLike;
    }

    public void setPositiveLike(int positiveLike) {
        this.positiveLike = positiveLike;
    }

    public int getNegativeLike() {
        return negativeLike;
    }

    public void setNegativeLike(int negativeLike) {
        this.negativeLike = negativeLike;
    }
}
