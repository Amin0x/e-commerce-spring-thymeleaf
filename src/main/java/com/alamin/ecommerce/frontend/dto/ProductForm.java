package com.alamin.ecommerce.frontend.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.product.ProductForm}
 * record, used to create / update products through the admin product client.
 */
public class ProductForm {

    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer initPrice;
    private String sku;
    private Integer stock;
    private Integer totalSold;
    private Boolean active;
    private Boolean enabled;
    private Integer category;
    private List<MultipartFile> images;
    private MultipartFile primaryImage;

    public ProductForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getInitPrice() {
        return initPrice;
    }

    public void setInitPrice(Integer initPrice) {
        this.initPrice = initPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Integer totalSold) {
        this.totalSold = totalSold;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public MultipartFile getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(MultipartFile primaryImage) {
        this.primaryImage = primaryImage;
    }
}
