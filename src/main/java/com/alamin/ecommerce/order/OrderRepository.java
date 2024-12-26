package com.alamin.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can define custom queries here if needed, e.g., based on customer name
    // List<Order> findByCustomerName(String customerName);
    
    // Query to count orders created today
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    long countOrdersToday(LocalDate startOfDay, LocalDate endOfDay);

    // Query to count orders placed this year
    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = :year")
    long countOrdersThisYear(int year);
    
    
    // Query to count orders for the current month
    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('MONTH', o.orderDate) = :month " +
           "AND FUNCTION('YEAR', o.orderDate) = :year ")
    long countOrdersByMonthAndProductId(int month, int year);
}
