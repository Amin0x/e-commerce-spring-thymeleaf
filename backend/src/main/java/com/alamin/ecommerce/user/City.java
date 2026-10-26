package com.alamin.ecommerce.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nameAr;
    private int estimatedDelivery;
    private String estimatedDeliveryUnit;
    private String deliveryPriority;
    private double deliveryPrice;
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    public City() {
    }

    public City(String name, String nameAr, int estimatedDelivery, String estimatedDeliveryUnit, String deliveryPriority, double deliveryPrice, String status, State state, Country country) {
        this.name = name;
        this.nameAr = nameAr;
        this.estimatedDelivery = estimatedDelivery;
        this.estimatedDeliveryUnit = estimatedDeliveryUnit;
        this.deliveryPriority = deliveryPriority;
        this.deliveryPrice = deliveryPrice;
        this.status = status;
        this.state = state;
        this.country = country;
    }
}