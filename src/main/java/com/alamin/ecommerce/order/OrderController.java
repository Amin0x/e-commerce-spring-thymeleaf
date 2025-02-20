package com.alamin.ecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Page<Order> getAllOrders(
        @RequestParam(name = "p", defaultValue = "1") int page,
        @RequestParam(name = "s", defaultValue = "10") int size,
        @RequestParam(name = "o", defaultValue = "0") int order,
        @RequestParam(name = "a", defaultValue = "0") int sort
    ) {
        return orderService.getAllOrders(page, size, order, sort);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
