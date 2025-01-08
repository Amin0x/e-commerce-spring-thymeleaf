package com.alamin.ecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    

    public List<Order> getAllOrders(int page, int size, int order, boolean asc) {
   	// Create a PageRequest with pagination and sorting
	String orderCol = null;
	switch(order){
	case 1: orderCol = "orderDate"; break;
	case 2: orderCol = ""; break;
	case 3: orderCol = ""; break;
	default: orderCol = "orderDate"; 
	}
   		
	Sort sort = asc == true ? Sort.by(Sort.Order.asc(orderCol)) : Sort.by(Sort.Order.desc(orderCol));
   	PageRequest pageRequest = PageRequest.of(page, size, sort);

   	// Fetch the paged and sorted orders from the repository
   	Page<Order> orderPage = orderRepository.findAll(pageRequest);
    
   	// Return the list of orders (you can return the Page if you need pagination info)
   	return orderPage.getContent();
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
        // تحديد المضاعف المناسب بناءً على الرقم
        int multiple = (int) Math.pow(10, Math.floor(Math.log10(number)));

        // تقريب الرقم إلى أقرب مضاعف
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



}
