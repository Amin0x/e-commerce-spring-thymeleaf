package com.alamin.ecommerce.frontend.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.product.ProductDto}
 * DTO. The frontend duplicates this DTO (no shared module) so each
 * module compiles independently; the contract is kept in sync via the JSON
 * fixtures in backend/src/test/resources/contracts and RestTemplate will
 * deserialize backend JSON directly into this class.
 */
public class ProductDto {

    private Long productId;
    private String uuid;
    private String name;
    private String description;
    private Integer price;
    private Integer basePrice;
    private Integer priceUSD;
    private String sku;
    private String slug;
    private Integer stock;
    private Integer totalSold;
    private Integer viewCount;
    private Boolean active;
    private Boolean enabled;
    private String image;
    private List<ProductImage> productImages = new ArrayList<>();
    private Long categoryId;
    private String categoryName;
    private Boolean newArrive;
    private Double percent;

    public ProductDto() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(Integer priceUSD) {
        this.priceUSD = priceUSD;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("newArrive")
    public Boolean getNewArrive() {
        return newArrive;
    }

    public void setNewArrive(Boolean newArrive) {
        this.newArrive = newArrive;
    }

    /**
     * Thymeleaf-compatible getter for {@link #getNewArrive()}.
     * Allows template expressions like {@code ${product.newArrive}} to work.
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Boolean getNew() {
        return getNewArrive();
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}