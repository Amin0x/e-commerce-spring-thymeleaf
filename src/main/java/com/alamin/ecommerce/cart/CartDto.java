package com.alamin.ecommerce.cart;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartDto {

    private Long cartId;
    private int total;
    private String sessionId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String userId;
    private List<CartItemDto> cartItems = new ArrayList<>();

    public CartDto() {
    }

    public CartDto(Cart cart) {
        this.cartId = cart.getCartId();
        this.total = cart.getTotal();
        this.sessionId = cart.getSessionId();
        this.created = cart.getCreated();
        this.updated = cart.getUpdated();
        this.userId = cart.getUserId();

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDto> cartItems) {
        this.cartItems = cartItems;
    }
}
