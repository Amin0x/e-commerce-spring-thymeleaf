package com.alamin.ecommerce.frontend.user;

import com.alamin.ecommerce.frontend.client.api.AdminUserClient;
import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin user management. Delegates to {@link AdminUserClient} which calls the
 * backend {@code /api/admin/users} REST endpoints.
 */
@Controller
@RequestMapping("")
public class AdminUserController {

    private final AdminUserClient adminUserClient;

    public AdminUserController(AdminUserClient adminUserClient) {
        this.adminUserClient = adminUserClient;
    }

    @GetMapping("/admin/users")
    public String index(Model model) {
        PageDto<UserDto> page = adminUserClient.list(0, 10);
        List<UserDto> users = page == null ? List.of() : page.getContent();

        model.addAttribute("newUsersList", users);
        model.addAttribute("topDrivers", users);
        model.addAttribute("totalUsers", page == null ? 0 : page.getTotalElements());
        model.addAttribute("activeUsers", page == null ? 0 : page.getTotalElements());
        model.addAttribute("newUsers", users.size());
        model.addAttribute("inactiveUsers", 0);
        model.addAttribute("usersRegistrationLabels", List.of());
        model.addAttribute("usersRegistrationData", List.of());
        model.addAttribute("visitorLabels", List.of());
        model.addAttribute("visitorData", List.of());

        model.addAttribute("user", new UserDto());
        model.addAttribute("pageTitle", "Admin Dashboard | Users Home Page");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin users page");
        model.addAttribute("pageTags", "orders,order,report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/users/users_index";
    }

    @GetMapping("/admin/users/all")
    public String allUsers(Model model) {
        PageDto<UserDto> page = adminUserClient.list(0, 100);
        model.addAttribute("users", page == null ? List.of() : page.getContent());
        model.addAttribute("pageNumber", 10);
        model.addAttribute("totalPages", page == null ? 10 : page.getTotalPages());
        model.addAttribute("i", 10);
        model.addAttribute("user", new UserDto());

        model.addAttribute("pageTitle", "Admin Dashboard | View All Users");
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin users page");
        model.addAttribute("pageTags", "orders,order,report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/users/users_list";
    }

    @GetMapping("/admin/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        UserDto user = adminUserClient.get(id);
        if (user == null) {
            return "error/404";
        }
        model.addAttribute("user", user);
        return "admin/users/user_details";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        UserDto user = adminUserClient.get(id);
        if (user == null) {
            return "error/404";
        }
        model.addAttribute("user", user);
        return "admin/users/user_edit_form";
    }

    @GetMapping("/admin/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "admin/users/user_create_form";
    }

    @PostMapping("/admin/users")
    public org.springframework.http.ResponseEntity<UserDto> createUser(
            @ModelAttribute UserDto user,
            @RequestParam(required = false) MultipartFile avatar) {

        if (user == null) {
            return org.springframework.http.ResponseEntity.badRequest().build();
        }
        UserDto savedUser = adminUserClient.create(user);
        return org.springframework.http.ResponseEntity.ok(savedUser);
    }

    @PostMapping("/admin/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute UserDto user,
                             BindingResult bindingResult, Model model) {
        if (user == null || bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult == null ? List.of() : bindingResult.getAllErrors());
            return "admin/users/user_edit_form";
        }

        UserDto savedUser = adminUserClient.update(id, user);
        model.addAttribute("user", savedUser);
        return "redirect:/admin/users/" + savedUser.getId();
    }
}
