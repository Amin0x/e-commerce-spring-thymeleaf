package com.alamin.ecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;


    public Page<Order> getAllOrders(int page, int size, int order, boolean asc, LocalDate startDate, LocalDate endDate) {
        // Create a PageRequest with pagination and sorting
        String orderCol = null;
        switch (order) {
            case 1:
                orderCol = "orderDate";
                break;
            case 2:
                orderCol = "";
                break;
            case 3:
                orderCol = "";
                break;
            default:
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

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
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


    @Scheduled(cron = "0 0 0 * * *")
    public void runAtMidnight() {
        System.out.println("Task is running at midnight!");
    }

    // run at midnight on the last day of the month
    @Scheduled(cron = "0 0 0 L * ?")
    public void runOnLastDayOfMonth() {
        System.out.println("Task is running on the last day of the month at midnight!");
    }


    public int grtOrderCountThisMonth() {
        return orderRepository.grtOrderCountThisMonth();
    }

    public Double getTotalRevenue() {
        Double totalRevenue = orderRepository.getTotalRevenue();
        return totalRevenue == null ? 0 : totalRevenue;
    }

    public Map<String, Object> getTotalRevenue(String d) {
        ArrayList<Double> res = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        if (d.equals("d")) {
            LocalDate currentDate = LocalDate.now();
            for (int i = 0; i < 12; i++) {
                LocalDate date = currentDate.plusDays(i);
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);;
                LocalDateTime startDate = date.atStartOfDay();
                LocalDateTime endDate = date.atTime(23, 59, 59);
                Double v = orderRepository.getTotalRevenue(startDate, endDate);
                res.add(v == null ? 0 : v);
                names.add(dayName);
            }


        } else if (d.equals("w")) {
            LocalDate currentDate = LocalDate.now();
            for (int i = 0; i < 12; i++) {
                LocalDate date = currentDate.plusWeeks(i);
                String dayName = String.valueOf(i);
                LocalDateTime startDate = date.atStartOfDay();
                LocalDateTime endDate = date.atTime(23, 59, 59);
                Double v = orderRepository.getTotalRevenue(startDate, endDate);
                res.add(v == null ? 0 : v);
                names.add(dayName);
            }
        } else if (d.equals("m")) {
            LocalDate currentDate = LocalDate.now();
            for (int i = 0; i < 12; i++) {
                LocalDate date = currentDate.plusMonths(i);
                String dayName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                LocalDateTime startDate = date.atStartOfDay();
                LocalDateTime endDate = date.atTime(23, 59, 59);
                Double v = orderRepository.getTotalRevenue(startDate, endDate);
                res.add(v == null ? 0 : v);
                names.add(dayName);
            }
        } else if (d.equals("y")) {
            LocalDate currentDate = LocalDate.now();
            for (int i = 0; i < 12; i++) {
                LocalDate date = currentDate.plusYears(i);
                String dayName = String.valueOf(date.getYear());
                LocalDateTime startDate = date.atStartOfDay();
                LocalDateTime endDate = date.atTime(23, 59, 59);
                Double v = orderRepository.getTotalRevenue(startDate, endDate);
                res.add(v == null ? 0 : v);
                names.add(dayName);
            }
        }
        Map<String , Object> map = new HashMap<>();
        map.put("data", res);
        map.put("names", names);
        return map;
    }

    public List<Order> getAllOrders(int page, int size, int order, boolean b) {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}
