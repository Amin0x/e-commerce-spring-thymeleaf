package com.alamin.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can define custom queries here if needed, e.g., based on customer name
    // List<Order> findByCustomerName(String customerName);
    
    // Query to count orders created today
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdDate >= :startOfDay AND o.createdDate < :endOfDay")
    long countOrdersToday(LocalDate startOfDay, LocalDate endOfDay);

    // Query to count orders placed this year
    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('YEAR', o.createdDate) = :year")
    long countOrdersThisYear(int year);
    
    
    // Query to count orders for the current month and a specific productId
    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('MONTH', o.createdDate) = :month " +
           "AND FUNCTION('YEAR', o.createdDate) = :year " +
           "AND o.productId = :productId")
    long countOrdersByMonthAndProductId(int month, int year, Long productId);
}
