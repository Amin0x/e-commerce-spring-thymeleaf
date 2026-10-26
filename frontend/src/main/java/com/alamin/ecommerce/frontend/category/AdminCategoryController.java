package com.alamin.ecommerce.frontend.category;

import com.alamin.ecommerce.frontend.client.api.AdminCategoryClient;
import com.alamin.ecommerce.frontend.client.api.CategoryClient;
import com.alamin.ecommerce.frontend.dto.CategoryDto;
import com.alamin.ecommerce.frontend.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin category management. Reads go through {@link CategoryClient} (public
 * {@code /api/categories}); mutating admin operations go through
 * {@link AdminCategoryClient} ({@code /api/admin/categories}).
 */
@Controller
@Slf4j
public class AdminCategoryController {

    private final CategoryClient categoryClient;
    private final AdminCategoryClient adminCategoryClient;

    public AdminCategoryController(CategoryClient categoryClient, AdminCategoryClient adminCategoryClient) {
        this.categoryClient = categoryClient;
        this.adminCategoryClient = adminCategoryClient;
    }

    @GetMapping("/admin/categories/create")
    public String createCategoryPage(Model model) {
        List<CategoryDto> categories = categoryClient.list();
        model.addAttribute("categories", categories != null ? categories : List.of());
        model.addAttribute("user", new UserDto());

        model.addAttribute("pageTitle", "Admin Dashboard | Create New Category");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin categories page");
        model.addAttribute("pageTags", "orders,order,report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/categories/category_create";
    }

    @GetMapping("/admin/categories")
    public String indexPage(Model model) {
        List<CategoryDto> categories = categoryClient.list();
        model.addAttribute("categories", categories != null ? categories : List.of());
        model.addAttribute("user", new UserDto());

        model.addAttribute("pageTitle", "Admin Dashboard | Categories Index");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin categories page");
        model.addAttribute("pageTags", "orders,order,report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/categories/category_home";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String updateCategoryPage(@PathVariable Long id, Model model) {
        CategoryDto category = categoryClient.getById(id);
        if (category == null) {
            return "error/404";
        }

        List<CategoryDto> categories = categoryClient.list();
        categories.removeIf(c -> c.getId() != null && c.getId().equals(id));

        model.addAttribute("category", category);
        model.addAttribute("categoryId", id);
        model.addAttribute("categories", categories);
        model.addAttribute("user", new UserDto());

        model.addAttribute("pageTitle", "Admin Dashboard | Edit Category: " + category.getName());
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin categories page");
        model.addAttribute("pageTags", "orders,order,report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/categories/category_edit";
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<Object> createCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) MultipartFile image) {

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            CategoryDto category = adminCategoryClient.create(name, description, parentId, image);
            response.put("status", HttpStatus.OK);
            response.put("message", "Category created successfully");
            response.put("category", category);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/admin/categories/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            CategoryDto category = categoryClient.getById(id);
            if (category != null) {
                response.put("status", HttpStatus.OK);
                response.put("category", category);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("status", HttpStatus.NOT_FOUND);
                response.put("message", "Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/admin/categories/{id}/image")
    public ResponseEntity<Object> updateCategoryImage(@PathVariable Long id, @RequestParam MultipartFile image) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            CategoryDto category = adminCategoryClient.updateImage(id, image);
            response.put("status", HttpStatus.OK);
            response.put("category", category);
            response.put("message", "successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/admin/categories/search/{search}")
    public ResponseEntity<Object> searchCategoryByName(@PathVariable String search) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<CategoryDto> category = categoryClient.search(search);
            if (category == null || category.isEmpty()) {
                response.put("status", HttpStatus.NOT_FOUND);
                response.put("message", "Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                response.put("status", HttpStatus.OK);
                response.put("categories", category);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long id, @RequestBody CategoryDto category) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            CategoryDto updatedCategory = adminCategoryClient.update(id, category);
            response.put("status", HttpStatus.OK);
            response.put("message", "Category updated successfully");
            response.put("category", updatedCategory);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {
        try {
            adminCategoryClient.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            log.error("something went wrong: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
