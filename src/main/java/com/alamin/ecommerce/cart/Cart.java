package com.alamin.ecommerce.cart;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CartItem> items;

    private int totalQuantity;
    private double totalPrice;

    public Cart(){}
    
}
