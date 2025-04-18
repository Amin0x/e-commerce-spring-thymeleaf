package com.alamin.ecommerce.home;

import com.alamin.ecommerce.product.*;
import com.alamin.ecommerce.subscribe.SubscribeService;
import io.netty.util.internal.StringUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    private final CategoryService categoryService;

    private final ProductService productService;

    private final SubscribeService subscribeService;

    public HomeController(CategoryService categoryService, ProductService productService, SubscribeService subscribeService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.subscribeService = subscribeService;
    }

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home(Model model) {
        // Fetch the top categories from the database
        // If no top categories are found, fetch all active categories
        List<Category> topCategories = categoryService.getTopCategories();
        if (topCategories.isEmpty() || topCategories.size() < 12) {
            topCategories = categoryService.getCategoriesByActive(true);
        } 

        if (topCategories.size() > 12) {
            topCategories = topCategories.subList(0, 12); // Limit to 12 categories
        } 
        
        model.addAttribute("categories", topCategories);
        model.addAttribute("products", getProducts());
        model.addAttribute("newProducts", getNewArrivalProducts(12));
        model.addAttribute("mustSellingProducts", productService.getBestSellingProducts(12));
        model.addAttribute("categoryList", categoryService.getRandomCategories());
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
        return "public/faq"; // Resolves to about.html in templates
    }

    @GetMapping("/privacy")
    public String privacyPage(Model model) {
        model.addAttribute("notification", "");
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "الاسئلة الشائعة");
        return "public/privacy"; // Resolves to about.html in templates
    }

    @GetMapping("/return-policy")
    public String policyPage(Model model) {
        model.addAttribute("notification", null);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "ألأسئلة الشائعة");
        return "public/return_policy"; // Resolves to about.html in templates
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribeToMailingList(@RequestParam String email, @RequestParam String name) {
        Map<String, Object> response = new HashMap<>();
        if (StringUtil.isNullOrEmpty(name) || StringUtil.isNullOrEmpty(email)){
            response.put("status", "");
            response.put("message", "name and email is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        try {
            subscribeService.subscribe(email, name);
        } catch (DataIntegrityViolationException e) {
            response.put("status", "");
            response.put("message", "email already exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e){
            response.put("status", "");
            response.put("message", "some thing went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.put("status", "success");
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    private List<Product> getNewArrivalProducts(int i){

        return productService.getLastAddedProductsRandom(i);
    }

    private List<ProductDto> getProducts(){

        List<Product> products = productService.getRandomProducts(12);
        List<ProductDto> result;

        result = products.stream().map(product -> {
            ProductDto productDto = new ProductDto(product);
            productDto.setNew(product.getCreated().isAfter(LocalDateTime.now().minusMonths(2)));
            if (product.getBasePrice() == 0) {
                productDto.setPercent(0.0f);
            } else {
                productDto.setPercent( (float) product.getPrice() / product.getBasePrice());
            }
            return productDto;
        }).toList();

        return result;
    }

}
