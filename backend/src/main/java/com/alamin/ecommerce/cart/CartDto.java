package com.alamin.ecommerce.cart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Read model for {@link Cart}. Kept in sync with the frontend contract fixture
 * ({@code contracts/cart.json}) so controllers never serialize JPA entities.
 */
public class CartDto {

    private Long id;
    private String uuid;
    private String status;
    private String currency;
    private int total;
    private String sessionId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String userId;
    private List<CartItemDto> cartItems = new ArrayList<>();

    public CartDto() {
    }

    public CartDto(Cart cart) {
        this.id = cart.getId();
        this.uuid = cart.getUuid();
        this.status = cart.getStatus();
        this.currency = cart.getCurrency();
        this.total = cart.getTotal();
        this.sessionId = cart.getSessionId();
        this.created = cart.getCreated();
        this.updated = cart.getUpdated();
        this.userId = cart.getUserId();
        this.cartItems = cart.getCartItems().stream().map(CartItemDto::new).toList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
