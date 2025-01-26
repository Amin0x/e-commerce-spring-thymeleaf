package com.alamin.ecommerce.order;

import com.alamin.ecommerce.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public String index(
            Model model
    ){

        model.addAttribute("user", new User());
        model.addAttribute("totalOrder", 10);
        model.addAttribute("totalSale", 10);
        model.addAttribute("orderCompleted", 10);
        model.addAttribute("orderUncompleted", 10);
        model.addAttribute("lastOrders", orderService.getAllOrders(1,10,1,true));
        model.addAttribute("totalOrder99999", 10);
        return "admin/orders/order_index";
    }

    @GetMapping("/all")
    public String allOrders(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "1") int order,
            @RequestParam(required = false, defaultValue = "true") boolean asc,
            Model model
    ){
        model.addAttribute("orders", orderService.getAllOrders(page, size, order, asc));
        model.addAttribute("pageNumber", 10);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        return "admin/orders/order_list";
    }

    @GetMapping("/{id}")
    public String orderDetails(){
        return "admin/orders/order_details";
    }
}
