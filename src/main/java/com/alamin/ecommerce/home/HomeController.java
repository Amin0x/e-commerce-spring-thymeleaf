package com.alamin.ecommerce.home;

import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.product.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        model.addAttribute("newProducts", getNewArrivalProducts());
        model.addAttribute("mustSellingProducts", getMustSellingProducts());

        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");

        return "public/home";
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Resolves to about.html in templates
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe() {
        return ResponseEntity.ok("success");
    }

    private List<Product> getMustSellingProducts(){
        return new ArrayList<>();
    }

    private List<Product> getSimilarProducts(Product product){
        return new ArrayList<>();
    }

    private List<Product> getNewArrivalProducts(){
        return new ArrayList<>();
    }

    private List<ProductDto> getProducts(){

        List<Product> products = productService.getRandomProducts(12);

        return products.stream().map(product -> {
            ProductDto productDto = new ProductDto(product);
            productDto.setNew(product.getCreated().isAfter(LocalDateTime.now().minusMonths(2)));
            productDto.setPercent(product.getInitPrice() == 0? 0.0f : (float) (product.getPrice() / product.getInitPrice()));
            return productDto;
        }).toList();
    }

}
