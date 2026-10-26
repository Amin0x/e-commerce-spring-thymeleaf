package com.alamin.ecommerce.frontend.dto;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.product.ProductImage}
 * entity JSON shape.
 */
public class ProductImage {

    private Long id;
    private String image;
    private String altText;
    private String caption;
    private String title;

    public ProductImage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
