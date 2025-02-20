package com.alamin.ecommerce.home;

import com.alamin.ecommerce.order.OrderService;
import com.alamin.ecommerce.user.User;
import com.alamin.ecommerce.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminIndexController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String index(@RequestParam(name = "dv", defaultValue = "m") String type, Model model){
        var user = new User();
        var activities = new ArrayList<Map<String,String>>();

        List<String> listLabels = new ArrayList<>();
        List<Double> listData = new ArrayList<>();
        orderService.getTotalRevenueMonth(listData, listLabels);


        model.addAttribute("user", user);
        model.addAttribute("statsUsers", userService.getUsersCount());
        model.addAttribute("usersThisMonth", userService.getUsersCount());
        model.addAttribute("statsOrders", orderService.grtOrderCountThisMonth());
        model.addAttribute("ordersThisMonth", orderService.grtOrderCountThisMonth());
        model.addAttribute("statsRevenue", orderService.getTotalRevenue());
        model.addAttribute("revenueThisMonth", orderService.getTotalRevenue());
        model.addAttribute("activities", activities);
        model.addAttribute("data", listData);
        model.addAttribute("labels", listLabels);
        model.addAttribute("orderChartLabels", null);
        model.addAttribute("orderChartData", null);
        model.addAttribute("pageTitle", "Admin Dashboard - Index");

        return "admin/admin_home";
    }

    @GetMapping("/create")
    public String create(){
        return "admin/create_links";
    }
}
