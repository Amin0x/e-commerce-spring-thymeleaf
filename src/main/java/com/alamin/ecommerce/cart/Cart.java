package com.alamin.ecommerce.cart;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;
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
    private Long userId;
    private LocalDateTime createdDate;

    public Cart(){
        createdDate = LocalDateTime.now();
    }
    
}
