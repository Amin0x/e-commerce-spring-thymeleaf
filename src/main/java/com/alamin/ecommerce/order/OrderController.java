package com.alamin.ecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders(
        @RequestParam(name = "p", defaultValue = "1") int page,
        @RequestParam(name = "s", defaultValue = "10") int size,
        @RequestParam(name = "o", defaultValue = "0") int order,
        @RequestParam(name = "a", defaultValue = "0") int desc
    ) {
        return orderService.getAllOrders(page, size, order, (desc == 0));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
