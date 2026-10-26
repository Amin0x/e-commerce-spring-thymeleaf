package com.alamin.ecommerce.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Entity
@Table(name = "tbl_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal tax;
    private BigDecimal shipping;
    private LocalDateTime orderDate;
    private LocalDateTime shippingDate;
    private LocalDateTime deliveryDate;
    private String billingFirstName;
    private String billingLastName;
    private String billingStreet;
    private String billingCity;
    private String billingState;
    private String billingCountry;
    private String billingPostalCode;
    private String billingPhone;
    private String billingEmail;
    private String shippingLastName;
    private String shippingEmail;
    private String shippingAddressLine2;
    private String shippingAddressLine1;
    private String shippingAddressType;
    private String shippingAddressId;
    @NotEmpty
    private String shippingFirstName;
    @NotEmpty
    private String shippingStreet;
    @NotEmpty
    private String shippingCity;
    @NotEmpty
    private String shippingState;
    @NotEmpty
    private String shippingCountry;
    private String shippingPostalCode;
    @NotEmpty
    private String shippingPhone;
    @NotEmpty
    private String paymentMethod;
    @NotEmpty
    private String cardNumber;
    private String paymentStatus;
    private String transactionId;
    private String carrier;
    private String trackingNumber;
    private String estimatedArrival;
    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    // Constructors, Getters, Setters

    // Default constructor
    public Order() {
    }

    // Constructor with parameters
    public Order(Customer customer, List<OrderItem> orderItems, BigDecimal totalAmount, String paymentMethod, String transactionId) {
        this.customer = customer;
        this.orderItems = orderItems;
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
