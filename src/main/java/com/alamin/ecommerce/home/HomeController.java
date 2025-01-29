package com.alamin.ecommerce.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import java.util.Map;
import java.util.List;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.product.ProductService;
import com.alamin.ecommerce.product.Product;


@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home(Model model) {
        List<Category> categories = categoryService.getRandomCategories();

        if (categories.isEmpty())
            return "error404";
            //throw new RuntimeException("no category");

        List<Product> products1 = productService.getRandomProductsByCategory(categories.get(0).getName());
        List<Product> products2 = productService.getRandomProductsByCategory(categories.get(1).getName());
        List<Product> products3 = productService.getRandomProductsByCategory(categories.get(2).getName());
        List<Product> products4 = productService.getRandomProductsByCategory(categories.get(3).getName());

        if(products1 != null){
            model.addAttribute("products1", products1);
            model.addAttribute("productsName1", categories.get(0).getName());
        }
        if(products2 != null){
            model.addAttribute("products2", products2);
            model.addAttribute("productsName2", categories.get(1).getName());
        }
        if(products3 != null){
            model.addAttribute("products3", products3);
            model.addAttribute("productsName3", categories.get(2).getName());
        }
        if(products4 != null){
            model.addAttribute("products4", products4);
            model.addAttribute("productsName4", categories.get(3).getName());
        }

        List<Product> newProducts = productService.getNewArrivalProducts();
        model.addAttribute("products", productService.getAllProducts(1, 30));
        model.addAttribute("newProducts", newProducts);

        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        
        // Return the name of the HTML view (without .html extension)
        return "public/home"; // This will resolve to src/main/resources/templates/home.html
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Resolves to about.html in templates
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe() {
        return ResponseEntity.ok("success");
    }

}
