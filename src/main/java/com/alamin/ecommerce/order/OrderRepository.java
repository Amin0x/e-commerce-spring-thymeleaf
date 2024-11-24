package com.alamin.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can define custom queries here if needed, e.g., based on customer name
    // List<Order> findByCustomerName(String customerName);
}
