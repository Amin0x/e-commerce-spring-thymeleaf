package com.alamin.ecommerce.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime shippingDate;

    @Embedded
    private Address shippingAddress;

    private String paymentMethod;
    private String transactionId;

    // Constructors, Getters, Setters

    // Default constructor
    public Order() {
        this.orderDate = LocalDateTime.now(); // Set order date to current time by default
        this.status = OrderStatus.PENDING; // Default status is PENDING
    }

    // Constructor with parameters
    public Order(Customer customer, List<OrderItem> orderItems, Address shippingAddress, BigDecimal totalAmount, String paymentMethod, String transactionId) {
        this.customer = customer;
        this.orderItems = orderItems;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.orderDate = LocalDateTime.now(); // Set order date to current time by default
        this.status = OrderStatus.PENDING; // Default status is PENDING
    }

    public void setOrderDate(LocalDateTime orderDate) {
        // Prevent changing the order date after creation, if not necessary
        if (this.orderDate == null) {
            this.orderDate = orderDate != null ? orderDate : LocalDateTime.now();
        }
    }

    // Helper method to mark the order as shipped and set the shipping date
    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
        this.shippingDate = LocalDateTime.now();
    }

}
