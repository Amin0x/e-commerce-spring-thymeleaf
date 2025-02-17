package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormatSymbols;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    

    @GetMapping
    public String index(Model model) {

        var topUsers = userService.getLastUsers(10);

        model.addAttribute("user", new User());
        model.addAttribute("totalUsers", userService.getUsersCount());
        model.addAttribute("totalDrivers", 567234);
        model.addAttribute("usersThisMonth", userService.getUsersThisMonth());
        model.addAttribute("driversThisMonth", 567234);
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("topDrivers", topUsers);
        model.addAttribute("usersRegistrationLabels", 9876);
        model.addAttribute("usersRegistrationData", 9876);
        model.addAttribute("driversRegistrationLabels", 9876);
        model.addAttribute("driversRegistrationData", 9876);
        return "admin/users/users_index";
    }

    @GetMapping("/all")
    public String allUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pageNumber", 10);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        model.addAttribute("user", new User());
        return "admin/users/users_list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = null;
        try {
            user = userService.findUserById(id).orElseThrow();
        } catch (Exception e) {
            return "error/404";
            //throw new RuntimeException(e);
        }
        model.addAttribute("user", user);
        return "admin/users/user_details";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = null;
        try {
            user = userService.findUserById(id).orElseThrow();
        } catch (Exception e) {
            return "error/404";
            //throw new RuntimeException(e);
        }
        model.addAttribute("user", user);
        return "admin/users/user_edit_form";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        String[] months = new DateFormatSymbols().getMonths();
        model.addAttribute("monthNames", months);
        model.addAttribute("user", new User());
        
        return "admin/users/user_create_form";
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, Model model) {
        if (user == null) {
            return "admin/users/user_edit_form";
        }

        User savedUser = userService.updateUser(id, user);
        model.addAttribute("user", savedUser);

        return "redirect:/admin/users/" + savedUser.getUserId();
    }
}
