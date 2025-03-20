package com.alamin.ecommerce.cart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartDto {

    private Long cartId;
    private int total;
    private LocalDateTime created;
    private LocalDateTime updated;
    private List<CartItemDto> cartItems = new ArrayList<>();

    public CartDto() {
    }

    public CartDto(Cart cart) {
        this.cartId = cart.getCartId();
        this.total = cart.getTotal();
        this.created = cart.getCreated();
        this.updated = cart.getUpdated();

        this.cartItems = cart.getCartItems().stream().map(CartItemDto::new).toList();
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public List<CartItemDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDto> cartItems) {
        this.cartItems = cartItems;
    }
}
