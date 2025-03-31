package com.alamin.ecommerce.category;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    ProductService productService;


    // Get all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    // Get a category by ID
    @GetMapping("/{id}")
    public String getCategoryById(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty())
            return "error/404";

        List<Product> products = productService.getRandomProductsByCategory(id, 24);

        model.addAttribute("products", products);
        model.addAttribute("category", category.get());
        model.addAttribute("username", null);
        return "public/category";
    }

    @GetMapping("/{slug}")
    public String getCategoryProducts(@PathVariable String id, Model model) {
        Optional<Category> categoryOptional = categoryService.findByCategorySlug(id);
        if (categoryOptional.isEmpty())
            return "error/404";
        
        Category category = categoryOptional.get();

        List<Product> products = productService.getRandomProductsByCategory(category.getId(), 24);

        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("username", null);
        return "public/category";
    }


}
