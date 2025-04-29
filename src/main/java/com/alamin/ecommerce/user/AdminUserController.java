package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

@Controller
@RequestMapping("")
public class AdminUserController {


    @Autowired
    private UserService userService;
    

    @GetMapping("/admin/users")
    public String index(Model model) {

        var newUsersList = userService.getLastRegisteredUsers(10);
        var usersRegistrationLabels = userService.getUsersRegistrationLabelsMonth();
        var usersRegistrationData = userService.getUsersRegistrationMonth();
        var visitorLabels = new ArrayList<>();
        var visitorData = new ArrayList<>();

        model.addAttribute("user", new User());
        model.addAttribute("totalUsers", userService.getUsersCount());
        model.addAttribute("activeUsers", userService.getEnabledUsersCount());
        model.addAttribute("newUsers", userService.getCreatedUsersCountThisMonth());
        model.addAttribute("inactiveUsers", userService.getDisabledUsersCount());
        model.addAttribute("newUsersList", newUsersList);
        model.addAttribute("topDrivers", newUsersList);
        model.addAttribute("usersRegistrationLabels", usersRegistrationLabels);
        model.addAttribute("usersRegistrationData", usersRegistrationData);
        model.addAttribute("visitorLabels", visitorLabels);
        model.addAttribute("visitorData", visitorData);

        model.addAttribute("pageTitle", "Admin Dashboard | Users Home Page");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/users/users_index";
    }

    @GetMapping("/admin/users/all")
    public String allUsers(Model model) {

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("pageNumber", 10);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        model.addAttribute("user", new User());

        model.addAttribute("pageTitle", "Admin Dashboard | View All Users");
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/users/users_list";
    }

    @GetMapping("/admin/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = null;
        try {
            user = userService.getUserById(id).orElseThrow();
        } catch (Exception e) {
            return "error/404";
            //throw new RuntimeException(e);
        }
        model.addAttribute("user", user);
        return "admin/users/user_details";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = null;
        try {
            user = userService.getUserById(id).orElseThrow();
        } catch (Exception e) {
            return "error/404";
            //throw new RuntimeException(e);
        }
        model.addAttribute("user", user);
        return "admin/users/user_edit_form";
    }

    @GetMapping("/admin/users/create")
    public String createUserForm(Model model) {
        String[] months = new DateFormatSymbols().getMonths();
        model.addAttribute("monthNames", months);
        model.addAttribute("user", new User());
        
        return "admin/users/user_create_form";
    }

    @PostMapping("/admin/users")
    public ResponseEntity<User> createUser(
            @ModelAttribute User user,
            @RequestParam(required = false) MultipartFile avatar) {

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/admin/users/{id}")
    public String updateUser(@PathVariable Long id,@Valid @ModelAttribute User user,BindingResult bindingResult, Model model) {
        if (user == null) {
            return "admin/users/user_edit_form";
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "admin/users/user_edit_form";
        }
        
        User savedUser = userService.updateUser(id, user);
        model.addAttribute("user", savedUser);

        return "redirect:/admin/users/" + savedUser.getId();
    }
}
