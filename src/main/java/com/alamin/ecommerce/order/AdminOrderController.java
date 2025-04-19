package com.alamin.ecommerce.order;

import com.alamin.ecommerce.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/orders")
    public String index( Model model  ){

        model.addAttribute("user", new User());
        model.addAttribute("totalOrder", 10);
        model.addAttribute("totalSale", 10);
        model.addAttribute("orderCompleted", 10);
        model.addAttribute("orderUncompleted", 10);
        model.addAttribute("lastOrders", orderService.getAllOrders(1,10,1,1));

        return "admin/orders/order_index";
    }

    @GetMapping("/admin/orders/all")
    public String allOrders(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "1") int order,
            @RequestParam(required = false, defaultValue = "true") boolean asc,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ){
        model.addAttribute("orders", orderService.getAllOrders(page, size, order, asc, startDate, endDate));
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        return "admin/orders/order_list";
    }

    @GetMapping("/admin/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model){
        Order order = orderService.getOrderById(id).orElseThrow();
        Double total = 0.0;
        String estimatedDelivery = "";
        String customerAddress = "";

        model.addAttribute("order", order);
        model.addAttribute("total", total);
        model.addAttribute("estimatedDelivery", estimatedDelivery);
        model.addAttribute("customerAddress", customerAddress);
        return "admin/orders/order_details";
    }

    @DeleteMapping("/admin/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
