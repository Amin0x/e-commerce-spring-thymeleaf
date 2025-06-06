package com.alamin.ecommerce.stock;

import jakarta.persistence.*;
import lombok.*;
import com.alamin.ecommerce.product.Product;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private int price;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @OneToOne
    @JoinColumn(name ="product_id")
    private Product product;

    public Inventory() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    public Inventory(int quantity, int price, Product product) {
        this.quantity = quantity;
        this.price = price;
        this.product = product;
        this.createDate = LocalDateTime.now();
        this.updateDate = null;
    }
}
