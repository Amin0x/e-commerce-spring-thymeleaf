package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.ProductDto;

public class CartItemDto {
    private Long id;
    private int quantity;
    private int price;
    private int total;
    private ProductDto product;

    public CartItemDto() {
    }

    public CartItemDto(CartItem cartItem) {
        this.id = cartItem.getId();
        this.quantity  = cartItem.getQuantity();
        this.price = cartItem.getPrice();
        this.total = cartItem.getTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }
}
