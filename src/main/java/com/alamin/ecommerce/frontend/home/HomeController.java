package com.alamin.ecommerce.frontend.home;

import com.alamin.ecommerce.frontend.client.api.CategoryClient;
import com.alamin.ecommerce.frontend.client.api.ProductClient;
import com.alamin.ecommerce.frontend.dto.CategoryDto;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Public home page. All data now comes from the backend REST API through the
 * client layer instead of backend services directly.
 */
@Controller
public class HomeController {

    private final ProductClient productClient;
    private final CategoryClient categoryClient;

    public HomeController(ProductClient productClient, CategoryClient categoryClient) {
        this.productClient = productClient;
        this.categoryClient = categoryClient;
    }

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home(Model model) {
        // Top categories: fall back to all active when fewer than 12.
        List<CategoryDto> topCategories = categoryClient.top();
        if (topCategories == null || topCategories.isEmpty() || topCategories.size() < 12) {
            topCategories = categoryClient.active(true);
        }
        if (topCategories != null && topCategories.size() > 12) {
            topCategories = topCategories.subList(0, 12);
        }

        List<ProductDto> products = productClient.random(12);
        List<ProductDto> newProducts = productClient.newArrivals(12);
        List<ProductDto> mustSellingProducts = productClient.bestSelling(12);

        model.addAttribute("categories", topCategories);
        model.addAttribute("products", products);
        model.addAttribute("newProducts", newProducts != null ? newProducts : List.of());
        model.addAttribute("mustSellingProducts", mustSellingProducts != null ? mustSellingProducts : List.of());
        model.addAttribute("categoryList", topCategories);
        model.addAttribute("notification", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Possimus, natus. Nesciunt tempore nobis id hic. Nobis quaerat qui quibusdam repellendus!");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "الرئيسية");

        return "public/home";
    }

    @GetMapping("/offers")
    public String offersPage(Model model){
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
        model.addAttribute("pageTitle", "الاسئلة الشائعة");
        return "public/privacy";
    }

    @GetMapping("/return-policy")
    public String policyPage(Model model) {
        model.addAttribute("notification", null);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "ألأسئلة الشائعة");
        return "public/return_policy";
    }

    @GetMapping("/return_policy")
    public String policyPageUnderscore(Model model) {
        return policyPage(model);
    }

    @GetMapping("/help")
    public String helpPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "المساعدة");
        return "public/help";
    }
}
