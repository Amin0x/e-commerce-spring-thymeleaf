package com.alamin.ecommerce.frontend.order;

import com.alamin.ecommerce.frontend.client.api.AdminOrderClient;
import com.alamin.ecommerce.frontend.dto.OrderDto;
import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.UserDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Admin order management. Delegates to {@link AdminOrderClient} which calls the
 * backend {@code /api/admin/orders} REST endpoints.
 */
@Controller
public class AdminOrderController {

    private static final int DEFAULT_TOTAL_ORDERS = 10;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final AdminOrderClient adminOrderClient;

    public AdminOrderController(AdminOrderClient adminOrderClient) {
        this.adminOrderClient = adminOrderClient;
    }

    @GetMapping("/admin/orders")
    public String index(Model model) {

        PageDto<OrderDto> allOrders = adminOrderClient.list(0, DEFAULT_PAGE_SIZE, 0, 0);

        model.addAttribute("user", new UserDto());
        model.addAttribute("totalOrder", DEFAULT_TOTAL_ORDERS);
        model.addAttribute("totalSale", 10);
        model.addAttribute("orderCompleted", 10);
        model.addAttribute("orderUncompleted", 10);
        model.addAttribute("lastOrders", allOrders == null ? List.of() : allOrders.getContent());

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
        PageDto<OrderDto> orders = adminOrderClient.list(page, size, order, asc ? 0 : 1);

        model.addAttribute("orders", orders == null ? List.of() : orders.getContent());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", orders == null ? size : orders.getSize());
        model.addAttribute("totalPages", orders == null ? 1 : orders.getTotalPages());
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
        PageDto<OrderDto> orders = adminOrderClient.list(page, size, order, asc ? 0 : 1);
        model.addAttribute("orders", orders == null ? List.of() : orders.getContent());
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", orders == null ? 10 : orders.getTotalPages());
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
        PageDto<OrderDto> orders = adminOrderClient.list(page, size, order, asc ? 0 : 1);
        model.addAttribute("orders", orders == null ? List.of() : orders.getContent());
        model.addAttribute("pageNumber", page);
        model.addAttribute("totalPages", orders == null ? 10 : orders.getTotalPages());
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
        OrderDto order = adminOrderClient.get(id);
        if (order == null) {
            return "error/404";
        }
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
        adminOrderClient.delete(id);
        return ResponseEntity.noContent().build();
    }
}
