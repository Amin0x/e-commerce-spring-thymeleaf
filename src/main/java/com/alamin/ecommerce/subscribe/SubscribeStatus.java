package com.alamin.ecommerce.subscribe;

public enum SubscribeStatus {
    PENDING("pending"),
    VERIFIED("verified"),
    DELETED("deleted");

    private final String status;

    SubscribeStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
