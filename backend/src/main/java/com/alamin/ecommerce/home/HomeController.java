package com.alamin.ecommerce.home;

import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-rendered home / static pages.
 *
 * <p>Re-created so the pre-existing {@code HomeControllerTest} (moved from the
 * orphaned root {@code src/test} into {@code backend/src/test}) has a target
 * class to test against. View names and model attributes match the test's
 * expectations exactly.
 */
@Controller
public class HomeController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public HomeController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<ProductDto> products = productService.getRandomProducts(12);
        List<ProductDto> newProducts = productService.getLastAddedProductsRandom(12);
        List<ProductDto> mustSelling = productService.getBestSellingProducts(12);
        List<Category> categories = categoryService.getRandomCategories();

        model.addAttribute("products", products);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("mustSellingProducts", mustSelling);
        model.addAttribute("categoryList", categories);
        model.addAttribute("notification",
                "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Possimus, natus. Nesciunt tempore nobis id hic. Nobis quaerat qui quibusdam repellendus!");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "الرئيسية");
        return "public/home";
    }

    @GetMapping("/offers")
    public String offersPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "العروض والتخفيضات");
        return "public/offers";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "عن المتجر");
        return "public/about";
    }

    @GetMapping("/faq")
    public String faqPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "الاسئلة الشائعة");
        return "public/faq";
    }

    @GetMapping("/privacy")
    public String privacyPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "سياسة الخصوصية");
        return "public/privacy";
    }

    @GetMapping("/return_policy")
    public String policyPage(Model model) {
        model.addAttribute("notification", null);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "سياسة الاسترجاع");
        return "public/return_policy";
    }

    @GetMapping("/help")
    public String helpPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "المساعدة والدعم");
        return "public/help";
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribeToMailingList(
            @RequestParam String email,
            @RequestParam String name) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "success");
        body.put("message", "success");
        return ResponseEntity.ok(body);
    }
}
