package com.alamin.ecommerce.home;

import com.alamin.ecommerce.product.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;

import com.alamin.ecommerce.category.CategoryService;


@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home(Model model) {

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
    public ResponseEntity<String> subscribeToMailingList() {
        return ResponseEntity.ok("success");
    }

    private List<Product> getNewArrivalProducts(int i){

        return productService.getLastAddedProductsRandom(i);
    }

    private List<ProductDto> getProducts(){

        List<Product> products = productService.getRandomProducts(12);

        List<ProductDto> result =  products.stream().map(product -> {
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
