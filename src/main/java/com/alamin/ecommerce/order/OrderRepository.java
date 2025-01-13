package com.alamin.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can define custom queries here if needed, e.g., based on customer name
    // List<Order> findByCustomerName(String customerName);
    
    // Query to count orders created between tow date
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate < :endDate")
    long countOrdersToday(LocalDate startDate, LocalDate endDate);

    // Query to count orders placed this year
    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('YEAR', o.orderDate) = :year")
    long countOrdersThisYear(int year);
    
    
    // Query to count orders for the current month
    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('MONTH', o.orderDate) = :month " +
           "AND FUNCTION('YEAR', o.orderDate) = :year ")
    long countOrdersByMonthAndProductId(int month, int year);

    @Query("SELECT COUNT(o) FROM Order o WHERE FUNCTION('MONTH', o.orderDate) = FUNCTION('MONTH', NOW())")
    int grtOrderCountThisMonth();

    // Query to total orders created between tow date
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate >= :start AND o.orderDate <= :end")
    Double getTotalRevenue(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    Double getTotalRevenue();
}
