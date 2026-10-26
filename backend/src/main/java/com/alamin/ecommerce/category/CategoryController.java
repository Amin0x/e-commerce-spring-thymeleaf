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
    public String getAllCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("username", null);
        model.addAttribute("pageTitle", "Categories - E-commerce");
        model.addAttribute("pageDescription", "Explore our categories of products.");
        model.addAttribute("pageKeywords", "categories, products, ecommerce");
        model.addAttribute("pageUrl", "/categories");
        model.addAttribute("pageImage", "https://example.com/image.jpg"); // Replace with actual image URL
        model.addAttribute("pageType", "website"); // or "article" based on your content
        model.addAttribute("pageAuthor", "Your Name"); // Replace with actual author name
        model.addAttribute("pagePublishedTime", "2023-10-01T12:00:00Z"); // Replace with actual published time
        model.addAttribute("pageModifiedTime", "2023-10-01T12:00:00Z"); // Replace with actual modified time
        model.addAttribute("pageSection", "Categories"); // Replace with actual section name
        model.addAttribute("pageTags", "categories, products, ecommerce"); // Replace with actual tags
        model.addAttribute("pageLanguage", "en-US"); // Replace with actual language code
        model.addAttribute("pageCharset", "UTF-8"); // Replace with actual charset
        return "public/categories";
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
