package com.alamin.ecommerce.home;

import com.alamin.ecommerce.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminIndexController {

    @GetMapping
    public String index(Model model){
        var user = new User();
        var activities = new ArrayList<Map<String,String>>();


        model.addAttribute("user", user);
        model.addAttribute("statsUsers", 5000);
        model.addAttribute("statsOrders", 5000);
        model.addAttribute("statsRevenue", 5000);
        model.addAttribute("activities", activities);
        model.addAttribute("data", 5000);

        return "admin/home";
    }
}
