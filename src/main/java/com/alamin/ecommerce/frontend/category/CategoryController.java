package com.alamin.ecommerce.frontend.category;

import com.alamin.ecommerce.frontend.client.api.CategoryClient;
import com.alamin.ecommerce.frontend.client.api.ProductClient;
import com.alamin.ecommerce.frontend.dto.CategoryDto;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Public category pages. All data now comes from the backend REST API through
 * the client layer instead of backend services/repositories directly.
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryClient categoryClient;
    private final ProductClient productClient;

    public CategoryController(CategoryClient categoryClient, ProductClient productClient) {
        this.categoryClient = categoryClient;
        this.productClient = productClient;
    }

    // Get all categories
    @GetMapping
    public String getAllCategories(Model model) {
        List<CategoryDto> categories = categoryClient.list();
        model.addAttribute("categories", categories != null ? categories : List.of());
        model.addAttribute("username", null);
        model.addAttribute("pageTitle", "Categories - E-commerce");
        model.addAttribute("pageDescription", "Explore our categories of products.");
        model.addAttribute("pageKeywords", "categories, products, ecommerce");
        model.addAttribute("pageUrl", "/categories");
        model.addAttribute("pageImage", "https://example.com/image.jpg");
        model.addAttribute("pageType", "website");
        model.addAttribute("pageAuthor", "Your Name");
        model.addAttribute("pagePublishedTime", "2023-10-01T12:00:00Z");
        model.addAttribute("pageModifiedTime", "2023-10-01T12:00:00Z");
        model.addAttribute("pageSection", "Categories");
        model.addAttribute("pageTags", "categories, products, ecommerce");
        model.addAttribute("pageLanguage", "en-US");
        model.addAttribute("pageCharset", "UTF-8");
        return "public/categories";
    }

    // Get a category by ID
    @GetMapping("/{id}")
    public String getCategoryById(@PathVariable Long id, Model model) {
        CategoryDto category = categoryClient.getById(id);
        if (category == null) {
            return "error/404";
        }

        List<ProductDto> products = productClient.byCategory(id, 24);
        prefixImageUrls(products);

        model.addAttribute("products", products != null ? products : List.of());
        model.addAttribute("category", category);
        model.addAttribute("username", null);
        return "public/category";
    }

    @GetMapping("/{slug}")
    public String getCategoryProducts(@PathVariable String slug, Model model) {
        CategoryDto category = categoryClient.getBySlug(slug);
        if (category == null) {
            return "error/404";
        }

        List<ProductDto> products = productClient.byCategory(category.getId(), 24);
        prefixImageUrls(products);

        model.addAttribute("products", products != null ? products : List.of());
        model.addAttribute("category", category);
        model.addAttribute("username", null);
        return "public/category";
    }

    private void prefixImageUrls(List<ProductDto> products) {
        if (products == null) {
            return;
        }
        for (ProductDto p : products) {
            String image = p.getImage();
            if (image != null && !image.isBlank() && !image.startsWith("http")) {
                p.setImage("/uploads/images/" + image);
            }
        }
    }
}
