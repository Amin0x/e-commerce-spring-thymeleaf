package com.alamin.ecommerce.category;

import com.alamin.ecommerce.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/create")
    public String createCategoryPage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
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
        categories.removeIf((item)->{return Objects.equals(item.getCategoryId(), id);});
        model.addAttribute("categories", categories);
        model.addAttribute("user", new User());

        return "admin/categories/edit_category";
    }

    // Create a new category
    @PostMapping()
    public String createCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) Long parentId
    ) {
        try {
            Category parent = null;
            if (parentId != null)
                 parent = categoryService.getCategoryById(parentId).orElse(null);

            Category newCategory = new Category(name, description, parent);
            Category category = categoryService.createCategory(newCategory);
            return "redirect:/admin/categories/create";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/admin/categories/create";
        }
    }

//    // Get all categories
//    @GetMapping
//    public ResponseEntity<List<Category>> getAllCategories() {
//        return ResponseEntity.ok(categoryRepository.findAll());
//    }

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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    // Delete a category by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
