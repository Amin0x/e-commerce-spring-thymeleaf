package com.alamin.ecommerce.product;

import java.util.ArrayList;
import java.util.List;

public class ProductDto {

    private Long productId;
    private String name;
    private String description;
    private Integer price;
    private Integer initPrice;
    private Integer priceUSD;
    private String sku;
    private Integer stock;
    private Integer totalSold;
    private Integer viewCount;
    private Boolean active;
    private Boolean enabled;
    private String image;
    private List<ProductImage> productImages = new ArrayList<>();
    private Long categoryId;
    private boolean isNew;
    private float percent;

    public ProductDto() {
    }

    public ProductDto(Product product){
        this.productId = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.initPrice = product.getBasePrice();
        this.priceUSD = product.getPriceUSD();
        this.sku = product.getSku();
        this.stock = product.getStock();
        this.totalSold = product.getTotalSold();
        this.viewCount = product.getViewCount();
        this.image = product.getImage();
        this.productImages = product.getProductImages();
        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
        }
    }

    public ProductDto(String name,
                      Long productId,
                      String description,
                      Integer price,
                      Integer initPrice,
                      Integer priceUSD,
                      String sku,
                      Integer stock,
                      Integer totalSold,
                      Integer viewCount,
                      Boolean active,
                      Boolean enabled,
                      String image,
                      List<ProductImage> productImages,
                      Long categoryId,
                      boolean isNew,
                      float percent) {
        this.name = name;
        this.productId = productId;
        this.description = description;
        this.price = price;
        this.initPrice = initPrice;
        this.priceUSD = priceUSD;
        this.sku = sku;
        this.stock = stock;
        this.totalSold = totalSold;
        this.viewCount = viewCount;
        this.active = active;
        this.enabled = enabled;
        this.image = image;
        this.productImages = productImages;
        this.categoryId = categoryId;
        this.isNew = isNew;
        this.percent = percent;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public boolean getNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
