package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.user.User;
import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Data
@Table(name = "carts", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "productId"})})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    
    private int quantity;
    private int price;
    private Integer total;
    
    private Long productId;
    
    private LocalDateTime created;
    private LocalDateTime updated;

    @Column(name = "user_id")
    private String userId;

    public Cart(){
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }
    
}
