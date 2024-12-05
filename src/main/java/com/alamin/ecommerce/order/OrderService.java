package com.alamin.ecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    

    public List<Order> getAllOrders(int page, int size, int order) {
   		// Create a PageRequest with pagination and sorting
   		Sort sort = Sort.by(order == 1 ? Sort.Order.asc("date") : Sort.Order.desc("date"));
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
}
