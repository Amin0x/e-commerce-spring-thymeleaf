package com.alamin.ecommerce.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can define custom queries here if needed, e.g., based on customer name
    // List<Order> findByCustomerName(String customerName);
    
    // Query to count orders created between tow date
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.orderDate >= :startDate
        AND o.orderDate < :endDate""")
    long countOrdersToday(LocalDate startDate, LocalDate endDate);

    // Query to count orders placed this year
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE YEAR(o.orderDate) = YEAR(NOW())""")
    long countOrdersThisYear();

    // Query to count orders placed this month
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE MON(o.orderDate) = MON(NOW())
        AND YEAR(o.orderDate) = YEAR(NOW())""")
    long countOrdersThisMonth();
    
    
    // Query to count orders for the current month
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE FUNCTION('MONTH', o.orderDate) = :month
        AND FUNCTION('YEAR', o.orderDate) = :year""")
    long countOrdersByMonthAndProductId(int month, int year);

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE MONTH(o.orderDate) = MONTH(NOW())""")
    int grtOrderCountThisMonth();

    // Query to total orders created between tow date
    @Query("""
        SELECT SUM(o.totalAmount) AS totalAmount
        FROM Order o
        WHERE o.orderDate >= :start
        AND o.orderDate <= :end""")
    Double getTotalRevenue(LocalDateTime start, LocalDateTime end);

    @Query(value = """
            WITH RECURSIVE calendar AS (
              SELECT DATE_ADD(LAST_DAY(CURRENT_DATE() - INTERVAL 1 MONTH), INTERVAL 1 DAY) AS date
              UNION ALL
              SELECT DATE_ADD(date, INTERVAL 1 DAY)
              FROM calendar
              WHERE DATE_ADD(date, INTERVAL 1 DAY) <= LAST_DAY(CURRENT_DATE())
            )
            SELECT calendar.date, sum(tbl_orders.total_amount) as total
            FROM calendar
            LEFT JOIN tbl_orders ON calendar.date = DATE(tbl_orders.order_date)
            WHERE MONTH(calendar.date) = MONTH(CURRENT_DATE())
            AND YEAR(calendar.date) = YEAR(CURRENT_DATE())
            group by calendar.date""", nativeQuery = true)
    List<Map<String, String>> getRevenueMonth();

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    Double getTotalRevenue();

    Page<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, PageRequest pageRequest);
}
