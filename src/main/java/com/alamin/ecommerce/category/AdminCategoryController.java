package com.alamin.ecommerce.category;

import com.alamin.ecommerce.exception.ResourceAlreadyExistsException;
import com.alamin.ecommerce.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@Slf4j
public class AdminCategoryController {

    @Autowired
    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/admin/categories/create")
    public String createCategoryPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("user", new User());

        return "admin/categories/category_create";
    }

    @GetMapping("/admin/categories")
    public String indexPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("user", new User());

        return "admin/categories/category_home";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String updateCategoryPage(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isEmpty()) {
            return "error/404";
        }
        model.addAttribute("category", category.get());
        model.addAttribute("categoryId", id);
        List<Category> categories = categoryService.findAll();
        categories.removeIf((item)->{return Objects.equals(item.getId(), id);});
        model.addAttribute("categories", categories);
        model.addAttribute("user", new User());

        return "admin/categories/category_edit";
    }

    // Create a new category
    @PostMapping("/admin/categories")
    public ResponseEntity<Object> createCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Category category = categoryService.createCategory(name, description, parentId, image);            
            response.put("status", HttpStatus.OK);
            response.put("message", "Category created successfully");
            response.put("category", category);
            return ResponseEntity.status(HttpStatus.OK).body(response);            

        }catch(ResourceAlreadyExistsException e) {
            response.put("status", HttpStatus.BAD_REQUEST);
            response.put("message", "Category already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } 
    }

    // Get a category by ID
    @GetMapping("/admin/categories/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            if (category.isPresent()) {
                response.put("status", HttpStatus.OK);
                response.put("category", category.get());
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
    public ResponseEntity<Object> updateCategoryImage(@PathVariable Long id, @RequestParam MultipartFile image){
        Map<String, Object> response = new HashMap<>();
        try {
            Category category = categoryService.updateCategoryImage(id, image);
            response.put("status", HttpStatus.OK);
            response.put("category", category);
            response.put("message", "successfully");            
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/admin/categories/search/{search}")
    public ResponseEntity<Object> searchCategoryByName(@PathVariable String search) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Category> category = categoryService.searchCategoryByName(search);
            if (category.isEmpty()) {
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

    // Update an existing category
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long id, @ModelAttribute CategoryDto category) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category updateCategory = categoryService.updateCategory(id, category);
            response.put("status", HttpStatus.OK);
            response.put("message", "Category updated successfully");
            response.put("category", updateCategory);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete a category by ID
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {     
        
        try{
            categoryService.deleteCategory(id);

        } catch (Exception e) {
            log.error(" something went wrong: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } 
        
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
