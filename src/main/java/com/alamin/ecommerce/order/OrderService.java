package com.alamin.ecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    //todo: remove save or make private
    private Order saveOrder(Order order) {

        return orderRepository.save(order);
    }

    public Order createOrder(OrderDto orderDetails) {

        Order order = new Order();

        //order.setOrderItems();
        order.setCarrier("");
        order.setCardNumber("");
        order.setEstimatedArrival("");
        order.setPaymentMethod("");
        order.setPaymentStatus("");
        order.setShipping(BigDecimal.valueOf(0.0));
        order.setStatus(OrderStatus.PENDING);
        order.setTax(BigDecimal.valueOf(0));
        order.setTotalAmount(BigDecimal.valueOf(0));
        order.setTransactionId("");
        Address address = new Address();
        address.setState("khartoum");
        address.setCity("khartoum");
        address.setCountry("sudan");
        address.setPostalCode("111111");
        Customer customer = new Customer(orderDetails.firstName(), orderDetails.lastName(), address, null);
        customer.setEmail("test@test.com");
        order.setCustomer(customer);

        return orderRepository.save(order);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderById(Long id) {
        return this.findById(id);
    }

    public Page<Order> getAllOrders(int page, int size, int order, int sort) {
        if (size < 1 || page < 0 || order < 1 )
            throw new IllegalArgumentException("that's not how it works");

        String s = "";
        Sort sort1;

        if (order == 1) {
            sort1 = Sort.by(sort == 0?Sort.Direction.ASC:Sort.Direction.DESC, "orderDate");
        } else if (order == 2) {
            sort1 = Sort.by(sort == 0?Sort.Direction.ASC:Sort.Direction.DESC, "totalAmount");
        } else {
            sort1 = Sort.unsorted();
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort1);
        return orderRepository.findAll(pageRequest);
    }

    public Page<Order> getAllOrders(int page, int size, int order, boolean asc, LocalDate startDate, LocalDate endDate) {
        // Create a PageRequest with pagination and sorting
        String orderCol;
        if (order == 1) {
            orderCol = "orderDate";
        } else if (order == 2) {
            orderCol = "";
        } else if (order == 3) {
            orderCol = "";
        } else {
            orderCol = "orderDate";
        }

        Sort sort = asc ? Sort.by(Sort.Order.asc(orderCol)) : Sort.by(Sort.Order.desc(orderCol));
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (startDate != null && endDate != null) {
            return orderRepository.findByOrderDateBetween(startDate, endDate, pageRequest);
        } else {
            return orderRepository.findAll(pageRequest);
        }
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setOrderDate(orderDetails.getOrderDate());
            return orderRepository.save(order);
        } else {
            return null;
        }
    }

    // Method to get the total number of orders for the current month
    public long getTotalOrdersThisMonth() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Call the repository to get the count of orders for the current month and the productId
        return orderRepository.countOrdersByMonthAndProductId(currentMonth, currentYear);
    }

    public static int roundToNearestMultiple(int number) {
        int multiple = (int) Math.pow(10, Math.floor(Math.log10(number)));
        return (int) (Math.round((double) number / multiple) * multiple);
    }


    public int grtOrderCountThisMonth() {
        return orderRepository.grtOrderCountThisMonth();
    }

    public Double getTotalRevenue() {
        Double totalRevenue = orderRepository.getTotalRevenue();
        return totalRevenue == null ? 0 : totalRevenue;
    }


    public void getTotalRevenueMonth(List<Double> listData, List<String> listLabels){
        ArrayList<Double> data = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDate = currentDate.with(TemporalAdjusters.lastDayOfMonth());

        while (currentDate.isBefore(lastDate)) {

            String dayName = String.valueOf(currentDate.getDayOfMonth());
            LocalDateTime startDate = currentDate.atStartOfDay();
            LocalDateTime endDate = currentDate.atTime(23, 59, 59);
            Double v = orderRepository.getTotalRevenue(startDate, endDate);
            data.add(v == null ? 0 : v);
            names.add(dayName);
            currentDate = currentDate.plusDays(1);
        }

        listData.addAll(data);
        listLabels.addAll(names);
    }

    public void getTotalRevenueYear(List<Double> listData, List<String> listLabels){
        ArrayList<Double> data = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDate = currentDate.with(TemporalAdjusters.lastDayOfYear());

        while (currentDate.isBefore(lastDate)) {

            String dayName = String.valueOf(currentDate.getDayOfMonth());
            LocalDateTime startDate = currentDate.atStartOfDay();
            LocalDateTime endDate = currentDate.atTime(23, 59, 59);
            Double v = orderRepository.getTotalRevenue(startDate, endDate);
            data.add(v == null ? 0 : v);
            names.add(dayName);
            currentDate = currentDate.plusMonths(1);
        }

        listData.addAll(data);
        listLabels.addAll(names);
    }




}
