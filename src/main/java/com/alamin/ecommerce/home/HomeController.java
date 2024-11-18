package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home(Model model) {
        List<Category> categories = categoryService.getRandomCategories();
        /*Map<String, List<Category>> mapData = new HashMap<>();
        for (Category category:categories){
            List<Product> products = productService.getRandomProductsByCategory(category.getName());

            if(products != null)
                mapData.add(category.getName(), products);
        }

        model.addAttribute("data", mapData);*/
        
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
        model.addAttribute("newProducts", newProducts);
        // Return the name of the HTML view (without .html extension)
        return "home"; // This will resolve to src/main/resources/templates/home.html
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Resolves to about.html in templates
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String,Object>> subscribe() {
        return ResponseEntity.ok();
    }

}
