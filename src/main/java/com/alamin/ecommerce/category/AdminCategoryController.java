package com.alamin.ecommerce.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return "admin/categories/category_home";
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

    // Update an existing category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody String name,
            @RequestBody String description,
            @RequestBody String imageUrl,
            @RequestBody Long parentId
    ) {
        Category updateCategory = new Category(name, description, null);
        Category parent = categoryService.getCategoryById(parentId).orElseThrow();
        updateCategory.setParent(parent);
        updateCategory.setImageUrl(imageUrl);
        
        return ResponseEntity.ok(categoryService.updateCategory(id, updateCategory));
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
