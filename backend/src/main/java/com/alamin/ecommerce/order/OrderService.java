package com.alamin.ecommerce.order;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrder(OrderDto orderDetails);

    Optional<Order> getOrderById(Long id);

    Page<Order> getAllOrders(int page, int size, int order, int sort);

    Page<Order> getAllOrders(int page, int size, int order, boolean asc, LocalDate startDate, LocalDate endDate);

    void deleteOrder(Long id);

    Order updateOrder(Long id, Order orderDetails);

    // Method to get the total number of orders for the current month
    long getTotalOrdersThisMonth();

    int grtOrderCountThisMonth();

    Double getTotalRevenue();

    void getTotalRevenueMonth(List<Double> listData, List<String> listLabels);

    void getTotalRevenueYear(List<Double> listData, List<String> listLabels);
}
