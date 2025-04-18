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
    private String name;
    private String nameAr;
    private String estimatedDelivery;
    private String estimatedDeliveryUnit;
    private String deliveryPriority;
    private Double deliveryPrice;
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

}