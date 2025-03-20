package com.alamin.ecommerce.cart;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Data
@Table(name = "tbl_carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    private int total;
    private String sessionId;
    private LocalDateTime created;
    private LocalDateTime updated;
    //@Column(name = "user_id")
    private String userId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(){
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
}
