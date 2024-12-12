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

    private Integer totalPrice;
    private Long userId;
    private String sid;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Cart(){
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
}
