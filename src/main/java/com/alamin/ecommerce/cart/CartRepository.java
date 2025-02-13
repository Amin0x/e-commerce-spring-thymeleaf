package com.alamin.ecommerce.cart;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
   
    @Query(value = "SELECT c FROM Cart c WHERE c.userId = :userId")
    Cart findByUserId(String userId);


    @Query(value = "SELECT c FROM Cart c WHERE c.sessionId = :id")
    Cart getCartBySession(String id);
}
