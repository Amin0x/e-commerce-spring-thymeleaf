package com.alamin.ecommerce.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @GetMapping
    public String index(Model model) {

        var topUsers = new ArrayList<User>();

        model.addAttribute("totalUsers", 567234);
        model.addAttribute("totalDrivers", 567234);
        model.addAttribute("usersThisMonth", 567234);
        model.addAttribute("driversThisMonth", 567234);
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("topDrivers", topUsers);
        model.addAttribute("usersRegistrationLabels", 9876);
        model.addAttribute("usersRegistrationData", 9876);
        model.addAttribute("driversRegistrationLabels", 9876);
        model.addAttribute("driversRegistrationData", 9876);
        return "admin/users/index";
    }

    @GetMapping("/{id}")
    public String editForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/edit_form";
    }

    @PostMapping("/{id}")
    public String updateUser(@RequestBody User user, Model model) {

        return "admin/users/edit_form";
    }
}
