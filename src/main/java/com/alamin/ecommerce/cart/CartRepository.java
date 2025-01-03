package com.alamin.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query(value = "SELECT c FROM Cart c WHERE c.sid = :sessionId")
    Optional<Cart> findBySessionId(String sessionId);
}
