package com.alamin.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
   
    @Query(value = "SELECT c FROM Cart c WHERE c.userId = :userId")
    Cart findByUserId(String userId);


    @Query(value = "SELECT c FROM Cart c WHERE c.sessionId = :id")
    Cart getCartBySession(String id);
}
