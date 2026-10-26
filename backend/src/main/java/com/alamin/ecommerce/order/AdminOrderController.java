package com.alamin.ecommerce.order;

import com.alamin.ecommerce.user.User;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class AdminOrderController {

    public static final int DEFAULT_TOTAL_ORDERS = 10;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_ORDER = 1;
    public static final int DEFAULT_PAGE_SORT = 1;
    public static final int DEFAULT_PAGE_INDEX = 1;

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/orders")
    public String index( Model model  ){

        Page<Order> allOrders = orderService.getAllOrders(DEFAULT_PAGE_INDEX, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_ORDER, DEFAULT_PAGE_SORT);

        model.addAttribute("user", new User());
        model.addAttribute("totalOrder", DEFAULT_TOTAL_ORDERS);
        model.addAttribute("totalSale", 10);
        model.addAttribute("orderCompleted", 10);
        model.addAttribute("orderUncompleted", 10);
        model.addAttribute("lastOrders", allOrders);

        model.addAttribute("pageTitle", "Admin Dashboard | Orders Index");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders home page");
        model.addAttribute("pageTags", "orders,order");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

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
        Page<Order> orders = orderService.getAllOrders(page, size, order, asc, startDate, endDate);

        model.addAttribute("orders", orders.getContent());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", orders.getSize());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("sortAsc", asc);
        model.addAttribute("pageTitle", "Admin Dashboard | View All Orders");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders home page");
        model.addAttribute("pageTags", "orders,order");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/orders/order_list";
    }

    @GetMapping("/admin/orders/completed")
    public String completedOrders(@RequestParam(required = false, defaultValue = "0") int page,
                                  @RequestParam(required = false, defaultValue = "20") int size,
                                  @RequestParam(required = false, defaultValue = "1") int order,
                                  @RequestParam(required = false, defaultValue = "true") boolean asc,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  Model model){
        model.addAttribute("orders", orderService.getAllOrders(page, size, order, asc, startDate, endDate));
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        model.addAttribute("pageTitle", "Admin Dashboard | Completed Orders");
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders home page");
        model.addAttribute("pageTags", "orders,order");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/orders/order_list";
    }

    @GetMapping("/admin/orders/pending")
    public String pendingOrders(@RequestParam(required = false, defaultValue = "0") int page,
                                  @RequestParam(required = false, defaultValue = "20") int size,
                                  @RequestParam(required = false, defaultValue = "1") int order,
                                  @RequestParam(required = false, defaultValue = "true") boolean asc,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                  Model model){
        model.addAttribute("orders", orderService.getAllOrders(page, size, order, asc, startDate, endDate));
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        model.addAttribute("pageTitle", "Admin Dashboard | Pending Orders");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders home page");
        model.addAttribute("pageTags", "orders,order");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

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
        model.addAttribute("pageTitle", "Admin Dashboard | Order Details");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin order details");
        model.addAttribute("pageTags", "orders,order,details,reports");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/orders/order_details";
    }

    @GetMapping("/admin/orders/reports")
    public String orderReports(Model model){

        model.addAttribute("pageTitle", "Admin Dashboard | Orders Reports");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/reports/order_reports";
    }

    @DeleteMapping("/admin/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
