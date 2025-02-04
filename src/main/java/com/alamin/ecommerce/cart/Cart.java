package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.user.User;
import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Data
@Table(name = "tbl_carts", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "productId"})})
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "cart")
    private List<CartItem> cartItems;

    public Cart(){
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
}
