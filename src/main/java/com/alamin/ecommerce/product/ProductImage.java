package com.alamin.ecommerce.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    private Long id;
    private Long productId;
    private String image;

    public ProductImage(String image) {
        this.image = image;
    }

    public ProductImage(String image, Long productId) {
        this.image = image;
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
