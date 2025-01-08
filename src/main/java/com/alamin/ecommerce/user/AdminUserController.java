package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    

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

    @GetMapping("/all")
    public String allUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pageNumber", 10);
        model.addAttribute("totalPages", 10);
        model.addAttribute("i", 10);
        return "admin/users/users_list";
    }

    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "admin/users/view_user";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "admin/users/edit_form";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        String[] months = new DateFormatSymbols().getMonths();
        model.addAttribute("monthNames", months);
        model.addAttribute("user", new User());
        return "admin/users/create_form";
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, Model model) {
        if (user == null) {
            return "admin/users/edit_form";
        }

        User newUser = userRepository.findById(id).orElseThrow();

        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setRole(user.getRole());
        newUser.setBirthDate(user.getBirthDate());
        newUser.setEnabled(user.getEnabled());
        newUser.setStatus(user.getStatus());

        User savedUser = userRepository.save(newUser);
        model.addAttribute("user", savedUser);
        return "redirect:/admin/users/view/" + savedUser.getUserId();
    }
}
