package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") // Handles requests to the root URL ("/")
    public String home() {
        // Return the name of the HTML view (without .html extension)
        return "home"; // This will resolve to src/main/resources/templates/home.html
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Resolves to about.html in templates
    }

}
