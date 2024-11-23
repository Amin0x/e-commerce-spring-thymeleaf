package com.example.ecommerce.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        // Prevent changing the order date after creation, if not necessary
        if (this.orderDate == null) {
            this.orderDate = orderDate != null ? orderDate : LocalDateTime.now();
        }
    }

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Helper method to mark the order as shipped and set the shipping date
    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
        this.shippingDate = LocalDateTime.now();
    }

}
