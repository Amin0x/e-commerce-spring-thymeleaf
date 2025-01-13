package com.alamin.ecommerce.home;

import com.alamin.ecommerce.order.OrderService;
import com.alamin.ecommerce.user.User;
import com.alamin.ecommerce.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminIndexController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String index(Model model){
        var user = new User();
        var activities = new ArrayList<Map<String,String>>();


        model.addAttribute("user", user);
        model.addAttribute("statsUsers", userService.getUsersThisMonth());
        model.addAttribute("statsOrders", orderService.grtOrderCountThisMonth());
        model.addAttribute("statsRevenue", orderService.getTotalRevenue());
        model.addAttribute("activities", activities);
        model.addAttribute("data", orderService.getTotalRevenue("y"));

        return "admin/admin_home";
    }
}
