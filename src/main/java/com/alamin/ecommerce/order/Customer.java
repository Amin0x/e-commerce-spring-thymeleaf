package com.alamin.ecommerce.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import com.alamin.ecommerce.user.User;
import lombok.*;

@Data
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Embedded
    private Address address;  // Embedding the Address class

    // One-to-one relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Constructors
    public Customer() {
    }

    public Customer(String firstName, String lastName, Address address, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.user = user;
    }

}
