package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home(Model model) {
        List<Category> categories = categoryService.getRandomCategories();
        Map<String, List<Category>> mapData = new HashMap<>();
        for (Category category:categories){
            List<Product> products = productService.getRandomProductsByCategory(category.getName());
            mapData.add(category.getName(), products);
        }

        model.addAttribute("data", mapData);
        // Return the name of the HTML view (without .html extension)
        return "home"; // This will resolve to src/main/resources/templates/home.html
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Resolves to about.html in templates
    }

}
