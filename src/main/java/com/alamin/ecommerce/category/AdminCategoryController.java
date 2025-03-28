package com.alamin.ecommerce.category;

import com.alamin.ecommerce.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
@Slf4j
public class AdminCategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;
    

    @GetMapping("/create")
    public String createCategoryPage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("user", new User());

        return "admin/categories/create-category";
    }

    @GetMapping()
    public String indexPage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("user", new User());

        return "admin/categories/category_home";
    }

    @GetMapping("/edit/{id}")
    public String updateCategoryPage(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow());
        List<Category> categories = categoryRepository.findAll();
        categories.removeIf((item)->{return Objects.equals(item.getId(), id);});
        model.addAttribute("categories", categories);
        model.addAttribute("user", new User());

        return "admin/categories/edit_category";
    }

    // Create a new category
    @PostMapping()
    public String createCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) MultipartFile image
    ) {
        try {            

            Category category = categoryService.createCategory(name, description, parentId, image);
            if (category == null) {
                return "redirect:/admin/categories/create";
            }
            return "redirect:/admin/categories";

        } catch (Exception e) {
            log.error("", e);
            return "redirect:/admin/categories/create";
        }
    }

    // Get a category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/{search}")
    public ResponseEntity<List<Category>> searchCategoryByName(@PathVariable String search) {
        List<Category> category = categoryService.searchCategoryByName(search);
        return ResponseEntity.ok(category);
    }

    // Update an existing category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDto category) {
        try {
            categoryService.updateCategory(id, category);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    // Delete a category by ID
    @DeleteMapping("/{id}")
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
