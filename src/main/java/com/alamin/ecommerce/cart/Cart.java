package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.user.User;
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
    
    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CartItem> items;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer totalPrice;
    private String sid;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Cart(){
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
}
